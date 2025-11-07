package org.parasol.ai;

import jakarta.enterprise.context.ApplicationScoped;

import org.parasol.model.Claimant;
import org.parasol.model.claim.Claim;

import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;

@ApplicationScoped
public class ClaimantInfoService {
	@Tool("Gets information about a claimant given the claim number")
	public Claimant getClaimant(@P("The claim number") String claimNumber) {
		return Claim.find("claimNumber", claimNumber)
			.project(Claimant.class)
			.firstResultOptional()
			.orElseThrow(() -> new IllegalArgumentException("Claimant not found for claim number %s".formatted(claimNumber)));
	}

	@Tool("Gets the from email address for any outgoing emails")
	public String getFromEmailAddress() {
		return "claims@parasol.com";
	}
}
