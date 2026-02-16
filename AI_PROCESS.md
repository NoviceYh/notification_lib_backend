# AI Usage Process

This document describes how AI tools were used during the development of this project, the strategies followed, and the boundaries between AI assistance and human decision-making.

---

## AI Tools Used

### GitHub Copilot
- Used inside the IDE as an **inline coding assistant**.
- Mainly leveraged for:
    - Method and class skeleton generation
    - Boilerplate reduction
    - Javadoc suggestions
    - Autocompletion of repetitive code patterns

### ChatGPT (GPT-5.2)
- Used as a **design and reasoning assistant**.
- Assisted with:
    - Architectural decisions (Hexagonal Architecture, ports/adapters)
    - API design and validation strategies
    - Error modeling and domain-driven decisions
    - Test design and edge case analysis
    - Asynchronous design using `CompletableFuture`
    - Documentation review and refinement

---

## How AI Was Used in the Development Process

AI was used as a **collaborative assistant**, not as an automatic code generator.

The typical workflow was:
1. Manually analyze the requirement or design problem.
2. Ask targeted questions to AI to validate ideas or explore alternatives.
3. Decide on the final approach based on clarity, simplicity, and scope.
4. Implement and adapt the solution manually.
5. Review and refactor AI-suggested code to ensure correctness and consistency.

AI suggestions were treated as **proposals**, not final answers.

---

## Prompting Strategy

Instead of generic prompts, the interaction focused on:
- Breaking down problems into small, focused questions
- Asking *why* certain approaches were better than others
- Requesting explanations before requesting code
- Iterating on designs step by step

Examples of strategies used:
- “Explain the design trade-offs before showing code”
- “How would this be defended in a technical interview?”
- “What is intentionally out of scope for this challenge?”
- “How would this scale without overengineering?”

This approach helped keep the implementation aligned with real-world engineering practices.

---

## Decisions Made by Me vs AI Suggestions

### Decisions made by me
- Overall architecture and scope boundaries
- Error modeling and domain-specific error codes
- Deciding what features to implement vs document (e.g. retries)
- Final API shape and naming
- Validation rules and fail-fast behavior
- What was considered out of scope for the challenge

### AI-assisted decisions
- Refining architectural patterns
- Improving clarity and consistency of the API
- Suggesting clean async patterns with `CompletableFuture`
- Reviewing edge cases and test scenarios
- Improving documentation wording and structure

_All final decisions were reviewed and validated manually._

---

## Where AI Helped the Most

- Accelerated architectural exploration
- Reduced time spent on boilerplate
- Helped reason about extensibility and future-proof design
- Improved documentation quality and clarity
- Provided feedback from a “reviewer perspective”

---

## Where AI Was Not Used or Was Limited

- Core implementation logic was written and reviewed manually
- No code was blindly copied without understanding
- No production secrets or credentials were shared
- Performance tuning and validation rules were intentionally decided without automation

---

## Final Notes

AI was used as a **productivity and reasoning tool**, not as a replacement for engineering judgment.

The goal was to demonstrate:
- Clear thinking
- Intentional design
- Awareness of trade-offs
- Responsible use of AI as part of a modern development workflow
