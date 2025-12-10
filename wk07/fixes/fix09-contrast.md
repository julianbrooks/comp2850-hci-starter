# Fix 09: Delete Button Contrast (WCAG 1.4.3)

**Backlog ID**: 9
**WCAG Criterion**: 1.4.3 Contrast (Minimum, Level AA)
**Priority**: 9 (highest)

---

## Problem Statement
Delete button text color (#6c757d) on white background (#ffffff) = 4.2:1 contrast ratio.
**Fails**: WCAG AA requires 4.5:1 for normal text.

**Evidence**:
- axe DevTools: color-contrast (Serious)
- WebAIM Contrast Checker: 4.2:1 (Fail AA)
- Screenshot: `wk07/evidence/contrast-before.png`

---

## Target State
Contrast ratio ≥ 4.5:1 (AA) or ≥ 7:1 (AAA).

---

## Solution
Override Pico.css button color with darker gray or custom color.

**Option A**: Darker gray (#495057 = 7:1 contrast, passes AAA)
**Option B**: Custom blue (#1976d2 = 5.2:1 contrast, passes AA)

**Chosen**: Option A (darker gray) for consistency with Pico theme.

---

## Implementation

### Before (Current CSS)
Pico.css default:
```css
button {
  color: #6c757d; /* 4.2:1 contrast */
}
