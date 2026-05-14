package in.vishal.blooms.service;

import in.vishal.blooms.models.Blog;
import in.vishal.blooms.models.Category;
import in.vishal.blooms.repository.BlogRepository;
import in.vishal.blooms.repository.CategoryRepository;
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
    private final UserRepository userRepository;

    public RagService(ChatModel chatModel, BlogRepository blogRepository, CategoryRepository categoryRepository, UserRepository userRepository) {
        this.chatModel = chatModel;
        this.blogRepository = blogRepository;
        this.categoryRepository = categoryRepository;
        this.userRepository = userRepository;
    }

    public ApiResponse<Map<String, String>> chatWithBloomsData(String query) {
        try {
            // 1. LIVE DATA FETCH: Current Database ka state read kar rahe hain
            // Taki jo bhi naya add/delete hua ho wo real-time AI ko pata chale
            List<Blog> latestBlogs = blogRepository.findAll(PageRequest.of(0, 15, Sort.by(Sort.Direction.DESC, "createdDTTM"))).getContent();
            List<Category> latestCategories = categoryRepository.findAll(PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "createdDTTM"))).getContent();
            long totalUsers = userRepository.count();

            // 2. CONTEXT BUILDER: AI ke liye strict rules aur live data set karna
            StringBuilder context = new StringBuilder();
            context.append("You are the Blooms Application AI Assistant. You must answer user queries ONLY based on the following live data from the Blooms database. If the answer is not present in the data below, politely say 'I don't have information about this in the current Blooms database.' Do NOT hallucinate or provide generic knowledge.\n\n");

            context.append("--- LIVE BLOOMS APP DATA ---\n");
            context.append("Total Registered Users on the platform: ").append(totalUsers).append("\n\n");

            context.append("Latest Published Blogs:\n");
            if(latestBlogs.isEmpty()) {
                context.append("No blogs available right now.\n");
            } else {
                for(Blog b : latestBlogs) {
                    if("Published".equalsIgnoreCase(b.getStatus()) && b.isActive()) {
                        context.append("- Title: '").append(b.getTitle()).append("' | Snippet: ").append(b.getDescription()).append("\n");
                    }
                }
            }

            context.append("\nCategories Available:\n");
            if(latestCategories.isEmpty()) {
                context.append("No categories available.\n");
            } else {
                for(Category c : latestCategories) {
                    if(c.isActive()) {
                        context.append("- Name: '").append(c.getName()).append("' | Description: ").append(c.getDescription()).append("\n");
                    }
                }
            }

            // 3. FINAL PROMPT: Strict system context + user ka sawaal
            String finalPrompt = context.toString() + "\n\nUser Query: " + query;

            // 4. API CALL
            String aiReply = chatModel.call(finalPrompt);

            // 5. Response Return Karna
            Map<String, String> responseData = new HashMap<>();
            responseData.put("answer", aiReply);

            return new ApiResponse<>(true, "RAG response generated", responseData);
        } catch (Exception e) {
            log.error("RAG Chat Error: ", e);
            return new ApiResponse<>(false, "AI system failed to process the request: " + e.getMessage(), null);
        }
    }
}