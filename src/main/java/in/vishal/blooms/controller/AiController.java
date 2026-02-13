package in.vishal.blooms.controller;

import in.vishal.blooms.response.ApiResponse;
import in.vishal.blooms.service.AiService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/ai")
public class AiController {

    private final AiService aiService;

    public AiController(AiService aiService) {
        this.aiService = aiService;
    }

    // API 1: Content Generate karne ke liye
    // User URL me category, subcategory aur title pass karega
    @GetMapping("/generate-blog")
    public ApiResponse<String> generateBlog(
            @RequestParam String categoryName,
            @RequestParam String subCategoryName,
            @RequestParam String blogTitle) {

        // Service ko call kar rahe hain jo saara logic handle karega
        return aiService.generateBlogContent(categoryName, subCategoryName, blogTitle);
    }

    // API 2: Blog summarize karne ke liye
    // User URL me blog ka title pass karega
    @GetMapping("/summarize-blog")
    public ApiResponse<String> summarizeBlog(@RequestParam String blogTitle) {

        // Service ko call kar rahe hain summarise wale feature ke liye
        return aiService.summarizeBlogContent(blogTitle);
    }
}