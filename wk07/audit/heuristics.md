# Heuristic Evaluation — Week 7

**Evaluator**: Ningsong Jia
**Date**: 2025-11-14
**Method**: Nielsen's 10 Usability Heuristics + Shneiderman's Golden Rules

---

## Nielsen's Heuristics

### 1. Visibility of System Status
**Rating**: 4/5 (Good)
**Evidence**:
- ✅ Status messages announce add/delete/edit actions
- ✅ ARIA live region updates (`role="status"`)
- ⚠️ No loading indicator for HTMX requests (instant for now, but could be slow on poor network)

**Accessibility implication**: Screen readers get confirmation via live region (WCAG 4.1.3).

**Issue identified**: None (meets WCAG). Enhancement: Add `hx-indicator` for slow requests.

---

### 2. Match Between System and Real World
**Rating**: 5/5 (Excellent)
**Evidence**:
- ✅ Plain language: "Add Task", "Edit", "Delete" (not technical jargon)
- ✅ Confirmation messages in natural language: "Task 'Buy milk' added successfully"

**Accessibility implication**: Simple language benefits cognitive disabilities, low digital literacy.

**Issue identified**: None.

---

### 3. Customer Control and Freedom
**Rating**: 4/5 (Good)
**Evidence**:
- ✅ Cancel button in edit mode (escape hatch)
- ❌ No undo for delete (permanent action)

**Accessibility implication**: People with motor impairments may accidentally trigger delete.

**Issue identified**: **Medium severity** — Add confirmation dialog or undo feature for delete.

---

### 4. Consistency and Standards
**Rating**: 5/5 (Excellent)
**Evidence**:
- ✅ Semantic HTML (`<button>`, not `<div onclick>`)
- ✅ Follows ARIA patterns (errors use `role="alert"`)
- ✅ Consistent with GOV.UK patterns (error summary, hints)

**Accessibility implication**: Consistency reduces learning curve for AT.

**Issue identified**: None.

---

### 5. Error Prevention
**Rating**: 3/5 (Fair)
**Evidence**:
- ✅ Client-side `required` attribute prevents blank submission
- ⚠️ Server-side validation catches blank titles (good), but no prevention of accidental delete

**Accessibility implication**: BenefitS from preventing errors before they happen.

**Issue identified**: **Medium severity** — Delete button too easy to trigger accidentally (no confirmation).

---

### 6. Recognition Rather Than Recall
**Rating**: 4/5 (Good)
**Evidence**:
- ✅ Labels always visible (not just placeholders) — *Note: Exception found in WCAG audit (Priority input missing label)*
- ✅ Hint text persists below input (`aria-describedby`)
- ⚠️ Error message only appears after submission (could preview validation on blur)

**Accessibility implication**: Persistent labels help cognitive disabilities, memory impairments.

**Issue identified**: None (meets WCAG). Enhancement: Live validation on blur.

---

### 7. Flexibility and Efficiency of Use
**Rating**: 3/5 (Fair)
**Evidence**:
- ✅ Keyboard shortcuts work (Enter submits, Escape cancels in some browsers)
- ❌ No custom shortcuts (e.g., Ctrl+E to edit first task)

**Accessibility implication**: Experienced people using keyboards could benefit from shortcuts.

**Issue identified**: **Low severity** — Add keyboard shortcuts (defer to Semester 2).

---

### 8. Aesthetic and Minimalist Design
**Rating**: 5/5 (Excellent)
**Evidence**:
- ✅ No clutter (only essential fields shown)
- ✅ Edit form appears inline (progressive disclosure)
- ✅ Status messages dismiss automatically (don't accumulate)

**Accessibility implication**: Minimalism reduces cognitive load, SR navigation time.

**Issue identified**: None.

---

### 9. Help People Recognise, Diagnose, and Recover from Errors
**Rating**: 5/5 (Excellent)
**Evidence**:
- ✅ Error message specific: "Title is required. Please enter at least one character."
- ✅ Error programmatically associated (`aria-describedby`, `role="alert"`)
- ✅ Recovery path clear (fix input, resubmit)

**Accessibility implication**: Meets WCAG 3.3.1 (Error Identification, A) and 3.3.3 (Error Suggestion, AA).

**Issue identified**: None.

---

### 10. Help and Documentation
**Rating**: 2/5 (Poor)
**Evidence**:
- ❌ No help text beyond inline hints
- ❌ No "What is this?" links for fields

**Accessibility implication**: People with cognitive disabilities, people using the interface for the first time may struggle without documentation.

**Issue identified**: **Low severity** — Add help tooltips or links to docs (defer to Semester 2).

---

## Summary of Issues

| Heuristic | Issue | Severity | Inclusion Risk |
|-----------|-------|----------|----------------|
| 3 (Control & Freedom) | No undo for delete | Medium | Motor, Cognitive |
| 5 (Error Prevention) | Delete lacks confirmation | Medium | Motor, Cognitive |
| 7 (Flexibility) | No keyboard shortcuts | Low | Keyboard (experienced people) |
| 10 (Help) | No help documentation | Low | Cognitive, Low digital literacy |