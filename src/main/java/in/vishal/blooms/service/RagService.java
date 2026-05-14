package in.vishal.blooms.service;

import in.vishal.blooms.models.Blog;
import in.vishal.blooms.models.Category;
import in.vishal.blooms.models.SubCategory;
import in.vishal.blooms.models.User;
import in.vishal.blooms.repository.BlogRepository;
import in.vishal.blooms.repository.CategoryRepository;
import in.vishal.blooms.repository.SubCategoryRepository;
import in.vishal.blooms.repository.UserRepository;
import in.vishal.blooms.response.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class RagService {

    private static final Logger log = LoggerFactory.getLogger(RagService.class);
    private final ChatModel chatModel;
    private final BlogRepository blogRepository;
    private final CategoryRepository categoryRepository;
    private final SubCategoryRepository subCategoryRepository;
    private final UserRepository userRepository;

    public RagService(ChatModel chatModel, BlogRepository blogRepository,
                      CategoryRepository categoryRepository, SubCategoryRepository subCategoryRepository,
                      UserRepository userRepository) {
        this.chatModel = chatModel;
        this.blogRepository = blogRepository;
        this.categoryRepository = categoryRepository;
        this.subCategoryRepository = subCategoryRepository;
        this.userRepository = userRepository;
    }

    public ApiResponse<Map<String, String>> chatWithBloomsData(String query) {
        try {
            // 1. LIVE DATA FETCH: 15 ki limit set kar di hai sabke liye (Ultra Optimized for Showcase)
            PageRequest limit15 = PageRequest.of(0, 15);

            // Latest 15 records utha rahe hain Date & Time ke hisaab se
            List<Blog> blogs = blogRepository.findAll(PageRequest.of(0, 15, Sort.by(Sort.Direction.DESC, "createdDTTM"))).getContent();
            List<Category> categories = categoryRepository.findAll(PageRequest.of(0, 15, Sort.by(Sort.Direction.DESC, "createdDTTM"))).getContent();
            List<SubCategory> subCategories = subCategoryRepository.findAll(PageRequest.of(0, 15, Sort.by(Sort.Direction.DESC, "createdDTTM"))).getContent();
            List<User> users = userRepository.findAll(limit15).getContent();

            // 2. CONTEXT BUILDER: AI ke dimaag me pura A to Z data feed karna
            StringBuilder context = new StringBuilder();
            context.append("You are the Blooms Application AI Assistant. Answer the user's query based ONLY on the following live data from the Blooms database. You must provide answers in Hindi, Hinglish, or English as requested by the user. If asked for a summary or explanation of a blog, read its 'Full Content' below and explain beautifully. Correlate Author IDs to User Names when asked who wrote what.\n\n");

            // ---> USERS DETAIL
            context.append("=== USERS (Max 15) ===\n");
            for(User u : users) {
                if(u.isActive()) {
                    context.append("UserID: ").append(u.getId())
                            .append(" | Name: ").append(u.getName())
                            .append(" | Username: ").append(u.getUserName())
                            .append(" | Role: ").append(u.getRole())
                            .append(" | Email: ").append(u.getEmail())
                            .append("\n");
                }
            }

            // ---> CATEGORIES DETAIL
            context.append("\n=== CATEGORIES (Max 15) ===\n");
            for(Category c : categories) {
                if(c.isActive()) {
                    context.append("CatID: ").append(c.getId())
                            .append(" | Name: ").append(c.getName())
                            .append(" | Desc: ").append(c.getDescription())
                            .append(" | CreatedByUserId: ").append(c.getCreatedBy())
                            .append(" | Status: ").append(c.getStatus())
                            .append(" | Time: ").append(c.getCreatedDTTM())
                            .append("\n");
                }
            }

            // ---> SUBCATEGORIES DETAIL
            context.append("\n=== SUBCATEGORIES (Max 15) ===\n");
            for(SubCategory sc : subCategories) {
                if(sc.getActive()) {
                    context.append("SubCatID: ").append(sc.getId())
                            .append(" | Name: ").append(sc.getName())
                            .append(" | Desc: ").append(sc.getDescription())
                            .append(" | ParentCatID: ").append(sc.getCategoryId())
                            .append(" | CreatedByUserId: ").append(sc.getCreatedBy())
                            .append(" | Status: ").append(sc.getStatus())
                            .append(" | Time: ").append(sc.getCreatedDTTM())
                            .append("\n");
                }
            }

            // ---> BLOGS FULL DETAIL (A TO Z)
            context.append("\n=== BLOGS (Max 15 - FULL DETAILS) ===\n");
            for(Blog b : blogs) {
                if(b.isActive()) {
                    context.append("BlogID: ").append(b.getId())
                            .append("\nTitle: ").append(b.getTitle())
                            .append("\nStatus: ").append(b.getStatus())
                            .append("\nAuthorUserID: ").append(b.getAuthorId())
                            .append("\nCategoryID: ").append(b.getCategoryId())
                            .append("\nSubCategoryID: ").append(b.getSubcategoryId())
                            .append("\nCreation Time: ").append(b.getCreatedDTTM())
                            .append("\nShort Description: ").append(b.getDescription())
                            .append("\nFull Content: ").append(b.getContent())
                            .append("\n-------------------------\n");
                }
            }

            // 3. FINAL PROMPT: Data + User ka Sawal
            String finalPrompt = context.toString() + "\n\nUser Query: " + query;

            // 4. API CALL TO GEMINI
            String aiReply = chatModel.call(finalPrompt);

            // 5. Response Return Karna
            Map<String, String> responseData = new HashMap<>();
            responseData.put("answer", aiReply);

            return new ApiResponse<>(true, "RAG response generated successfully", responseData);
        } catch (Exception e) {
            log.error("RAG Chat Error: ", e);
            return new ApiResponse<>(false, "AI system failed to process the request: " + e.getMessage(), null);
        }
    }
}