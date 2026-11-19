package de.teammanagement.common;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class OpenApiDocumentationController {

	@GetMapping(value = "/openapi.yaml", produces = "application/yaml")
	public ResponseEntity<Resource> specification() {
		var specification = new ClassPathResource("api/openapi.yaml");
		if (!specification.exists()) {
			throw new IllegalStateException("Packaged OpenAPI contract is missing");
		}
		return ResponseEntity.ok()
				.contentType(MediaType.parseMediaType("application/yaml"))
				.body(specification);
	}
}
