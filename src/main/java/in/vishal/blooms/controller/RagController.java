package in.vishal.blooms.controller;

import in.vishal.blooms.response.ApiResponse;
import in.vishal.blooms.service.RagService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/rag")
public class RagController {

    private final RagService ragService;

    public RagController(RagService ragService) {
        this.ragService = ragService;
    }

    // Naya RAG endpoint jo frontend ke chatbot se connect hoga
    @PostMapping("/chat")
    public ResponseEntity<ApiResponse<Map<String, String>>> chat(@RequestBody Map<String, String> request) {
        String query = request.get("query");
        if (query == null || query.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(false, "Query empty nahi ho sakti bhai!", null));
        }
        return ResponseEntity.ok(ragService.chatWithBloomsData(query));
    }
}