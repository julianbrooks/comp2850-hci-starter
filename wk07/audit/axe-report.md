# axe DevTools Audit Report — Week 7

**Date**: 2025-11-14
**URL**: http://localhost:8080/tasks
**Tool**: axe DevTools 4.10.3
**Scope**: Full page scan (add form + task list)

---

## Summary
- **Critical**: 1
- **Serious**: 2
- **Moderate**: 0
- **Minor**: 0
- **Total**: 3 issues

---

## Serious/Critical Issues

### Issue 1: Images must have alternate text (Critical)
**Element**: `<img src="/static/img/icon.png" width="16" height="28.5">`
**Rule**: `image-alt` (WCAG 1.1.1 Non-text Content)
**Description**: Ensure images have alternate text. The image element does not have an `alt` attribute.
**Impact**: Screen reader users cannot determine the content or purpose of the image.
**Status**: Confirmed (This is the "minor issue" left in the scaffold for discovery)

### Issue 2: Insufficient color contrast (Serious)
**Element**: `<button type="submit">Add Task</button>`
**Rule**: `color-contrast` (WCAG 1.4.3 Contrast (Minimum))
**Description**: Element has insufficient color contrast of 1.11 (foreground color: #6c757d, background color: #0172ad, font size: 12.8pt). Expected contrast ratio of 4.5:1.
**Impact**: People with low vision struggle to read the text on the button.
**Status**: Confirmed

### Issue 3: Links must have discernible text (Serious)
**Element**: `<a href="/about"></a>`
**Rule**: `link-name` (WCAG 2.4.4 Link Purpose, 4.1.2 Name, Role, Value)
**Description**: Ensure links have discernible text. The link element is empty and has no aria-label.
**Impact**: Screen reader users will hear "link" or the URL but won't know the link's destination or purpose.
**Status**: Confirmed

---

## Actions
1. **High priority**: Remove the decorative icon image (or add alt text if kept). (Candidate for immediate fix)
2. **High priority**: Fix contrast ratio for the "Add Task" button (Change text color or background).
3. **High priority**: Add text content "About" or an `aria-label` to the footer link.