// Ye batata hai ki hamari ye java file kis folder (directory) me rakhi hui hai.
// Agar ye nahi likhenge toh Java ko pata hi nahi chalega ki ye file kahan belong karti hai aur error de dega.
package in.vishal.blooms.service;

// Niche ke saare 'import' statements isliye hain taaki hum dusri files (jaise Blog, Category, Repository) ko yahan use kar sakein.
// Agar inko nahi likhenge, toh compiler ko samajh nahi aayega ki 'Blog' ya 'Category' kya bimari hai aur 'Class Not Found' ka error aayega.
import in.vishal.blooms.models.Blog;
import in.vishal.blooms.models.Category;
import in.vishal.blooms.models.SubCategory;
import in.vishal.blooms.models.Status;
import in.vishal.blooms.repository.BlogRepository;
import in.vishal.blooms.repository.CategoryRepository;
import in.vishal.blooms.repository.SubCategoryRepository;
import in.vishal.blooms.response.ApiResponse;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

// @Service Spring Boot ko batata hai ki ye class hamare project ka "Dimaag" (Business Logic) hai.
// Agar ye nahi lagayenge toh Spring Boot is class ka object automatically nahi banayega, aur Controller me error aayega ki "AiService not found".
@Service
public class AiService {

    // Yahan humne apne AI (ChatModel) aur database se baat karne wale Repositories ke variables banaye hain.
    // 'private' ka matlab inhe is class ke bahar koi use nahi kar sakta (Security).
    // 'final' ka matlab ek baar inki value set ho gayi toh koi ise change nahi kar sakta. (Nahi lagayenge toh data overwrite hone ka risk rehta hai).
    private final ChatModel chatModel;
    private final CategoryRepository categoryRepository;
    private final SubCategoryRepository subCategoryRepository;
    private final BlogRepository blogRepository;

    // Ye Constructor hai. Ise 'Constructor Injection' kehte hain.
    // Jab Spring Boot start hota hai, toh wo khud se in sabhi Repositories aur AI Model ka object banakar is function ko de deta hai.
    // Agar ye nahi likhenge toh hamare variables null rahenge aur jab hum DB se data nikalenge toh 'NullPointerException' (sabse bada error) aa jayega.
    public AiService(ChatModel chatModel, CategoryRepository categoryRepository, SubCategoryRepository subCategoryRepository, BlogRepository blogRepository) {
        this.chatModel = chatModel;
        this.categoryRepository = categoryRepository;
        this.subCategoryRepository = subCategoryRepository;
        this.blogRepository = blogRepository;
    }

    // FEATURE 1: Naya Blog Content Generate Karna
    // Ye method user se Category ka naam, SubCategory ka naam aur Blog ka Title leta hai.
    public ApiResponse<String> generateBlogContent(String categoryName, String subCategoryName, String blogTitle) {

        // Jaisa humne pehle discuss kiya tha, database se saara data na aaye, sirf 1 record aaye (memory bachane ke liye).
        // Nahi lagayenge toh lakho data RAM me aa jayega aur server crash ho sakta hai.
        PageRequest pageRequest = PageRequest.of(0, 1);

        // ✅ FIX: Using the updated IgnoreCase method for Category
        // Ye line database me jaati hai aur check karti hai ki jo category user ne di hai, kya wo sach me database me PUBLISHED aur ACTIVE hai ya nahi.
        // .trim() isliye lagaya taaki agar user ne galti se space daal diya ho toh wo hat jaye.
        Page<Category> categoryPage = categoryRepository.findByNameContainingIgnoreCaseAndStatusIgnoreCaseAndActive(
                categoryName.trim(), Status.PUBLISHED.getDisplayName(), true, pageRequest);

        // Upar jo Page mila, usme se actual list (Category ka dabba) bahar nikala.
        List<Category> categoryList = categoryPage.getContent();

        // Agar list khali hai (matlab database me wo category mili hi nahi) toh hum process rok dete hain aur error message wapas bhej dete hain.
        // Agar ye nahi karte, toh AI aisi category ka blog bana deta jo hamari website me hai hi nahi!
        if (categoryList.isEmpty()) {
            return new ApiResponse<>(false, "Error: Category '" + categoryName + "' doesn't exist.", null);
        }

        // Same logic jaise category ka kiya, waisa hi Subcategory ko check karne ke liye.
        Page<SubCategory> subCategoryPage = subCategoryRepository.findByNameContainingIgnoreCaseAndStatusIgnoreCaseAndActive(
                subCategoryName.trim(), Status.PUBLISHED.getDisplayName(), true, pageRequest);
        List<SubCategory> subCategoryList = subCategoryPage.getContent();

        // Agar subcategory nahi mili toh error throw karo aur yahin se wapas (return) jao.
        if (subCategoryList.isEmpty()) {
            return new ApiResponse<>(false, "Error: SubCategory '" + subCategoryName + "' doesn't exist.", null);
        }

        // Ye wo asali message (Prompt) hai jo AI ko bheja jayega.
        // Isme hum user ke diye hue title, category aur subcategory ko English ke sentence me fit kar dete hain.
        String promptText = "Write a complete and attractive blog post titled '" + blogTitle.trim() +
                "'. This blog falls under the category of '" + categoryName +
                "' and subcategory '" + subCategoryName + "'. Please write a good introduction, body paragraphs, and a clear conclusion at the end.";

        // 'try-catch' block kisi bhi achanak aane wale error (exception) ko rokne ke liye hota hai.
        // Agar Google Gemini ka server down ho, ya API key galat ho, toh hamara pura code fatega nahi, balki 'catch' block aaram se error message dikha dega.
        try {
            // chatModel.call() actually internet ke through AI se baat karta hai aur uska jawab (lamba sa blog content) nikal kar lata hai.
            String aiResponse = chatModel.call(promptText);
            // Jab sab successfully ho jaye, toh true aur AI ka jawab return kar do.
            return new ApiResponse<>(true, "Blog generated successfully!", aiResponse);
        } catch (Exception e) {
            // Agar AI se baat karne me koi lafda hua, toh ye block chalega.
            return new ApiResponse<>(false, "AI generation failed: " + e.getMessage(), null);
        }
    }

    // FEATURE 2: Existing Blog ko Summarize karna aur improvement dena
    // Ye feature pehle se likhe hue blog ka title leta hai.
    public ApiResponse<String> summarizeBlogContent(String blogTitle) {

        // Fir se wahi, database se memory bachane ke liye sirf 1 record lana hai.
        PageRequest pageRequest = PageRequest.of(0, 1);

        // Database me check karna ki is title ka koi active aur published blog pada hai ya nahi.
        Page<Blog> blogPage = blogRepository.findByTitleContainingIgnoreCaseAndStatusIgnoreCaseAndActive(
                blogTitle.trim(), Status.PUBLISHED.getDisplayName(), true, pageRequest);

        // Page (Wrapper) me se List bahar nikalna.
        List<Blog> blogList = blogPage.getContent();

        // Agar database ne khali list di, matlab ye title ka blog exist nahi karta.
        if (blogList.isEmpty()) {
            // User ko saaf-saaf bata do ki bhai ye blog nahi hai, hum isko summarize nahi kar sakte.
            return new ApiResponse<>(false, "This blog is not present in the blog list so we cannot able to summarize this blog or give improvement suggestions.", null);
        }

        // Agar list khali nahi hai, toh pehle number (0 index) ka blog nikal lo.
        Blog existingBlog = blogList.get(0);

        // Us blog object ke andar se uska lamba wala content (jo usne likha tha) nikal lo.
        // Nahi nikalenge toh AI padhega kya?
        String blogContent = existingBlog.getContent();

        // AI ko command dena ki bhai pehle ise padh, fir short summary de, aur isko behtar banane ka tarika bata.
        // Uske baad \n\n (matlab 2 naye line chhod kar) asli blog ka content chipka (append) diya.
        String promptText = "Read the following blog content. First, provide a short and easy-to-understand summary. " +
                "Second, provide some actionable suggestions on how to improve this content.\n\n" +
                "Blog Content:\n" + blogContent;

        // Fir se try-catch taaki API fail ho toh code na fate.
        try {
            // AI ko prompt bhej kar response ka wait karna.
            String aiResponse = chatModel.call(promptText);
            // Response user (Swagger UI) ko wapas dikha dena.
            return new ApiResponse<>(true, "Blog summarized successfully!", aiResponse);
        } catch (Exception e) {
            // Agar AI summarize karne me fail hua toh yahan aa jayega.
            return new ApiResponse<>(false, "AI summarization failed: " + e.getMessage(), null);
        }
    }
}