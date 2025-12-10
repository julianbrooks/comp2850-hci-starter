# WCAG 2.2 AA Checklist — Week 7

**Date**: 2025-12-09
**Scope**: Task manager (add, edit, delete flows)
**Tester**: [Your Name]

---

## Perceivable (Principle 1)

### 1.1 Text Alternatives
| Criterion              | Level | Status | Evidence                           | Notes                                      |
|------------------------|-------|--------|------------------------------------|--------------------------------------------|
| 1.1.1 Non-text Content | A     | ❌ Fail | `<img>` src="/static/img/icon.png" | Image lacks `alt` attribute (Found by axe) |

### 1.3 Adaptable
| Criterion                    | Level | Status | Evidence                                    | Notes                                                         |
|------------------------------|-------|--------|---------------------------------------------|---------------------------------------------------------------|
| 1.3.1 Info and Relationships | A     | ❌ Fail | Priority input field                        | Missing `<label>` or `aria-label`. SR reads "edit text" only. |
| 1.3.2 Meaningful Sequence    | A     | ✅ Pass | Tab order: skip link → add form → task list | Logical reading order verified                                |

### 1.4 Distinguishable
| Criterion                | Level | Status | Evidence                               | Notes                                 |
|--------------------------|-------|--------|----------------------------------------|---------------------------------------|
| 1.4.3 Contrast (Minimum) | AA    | ❌ Fail | Add button #6c757d on #0172ad = 1.11:1 | Needs 4.5:1 (AA). Very hard to read.  |
| 1.4.11 Non-text Contrast | AA    | ✅ Pass | Focus outline visible                  | Sufficient contrast on inputs/buttons |

---

## Operable (Principle 2)

### 2.1 Keyboard Accessible
| Criterion              | Level | Status | Evidence                                    | Notes                             |
|------------------------|-------|--------|---------------------------------------------|-----------------------------------|
| 2.1.1 Keyboard         | A     | ✅ Pass | All features accessible via Tab/Enter/Space | Tested: add, edit, delete, cancel |
| 2.1.2 No Keyboard Trap | A     | ✅ Pass | No traps detected                           | Can Tab out of all forms          |

### 2.4 Navigable
| Criterion           | Level | Status | Evidence                                 | Notes                       |
|---------------------|-------|--------|------------------------------------------|-----------------------------|
| 2.4.1 Bypass Blocks | A     | ✅ Pass | Skip link appears on Tab, jumps to #main | Verified in Lab 1           |
| 2.4.3 Focus Order   | A     | ✅ Pass | Tab order: Edit → Title → Save → Cancel  | Logical sequence            |
| 2.4.7 Focus Visible | AA    | ✅ Pass | Blue outline visible on focus            | Default browser styles used |

---

## Understandable (Principle 3)

### 3.2 Predictable
| Criterion      | Level | Status | Evidence                       | Notes                             |
|----------------|-------|--------|--------------------------------|-----------------------------------|
| 3.2.1 On Focus | A     | ✅ Pass | No context change on focus     | Verified                          |
| 3.2.2 On Input | A     | ✅ Pass | No auto-submit on input change | Form submits only on button click |

### 3.3 Input Assistance
| Criterion                    | Level | Status | Evidence                               | Notes                                            |
|------------------------------|-------|--------|----------------------------------------|--------------------------------------------------|
| 3.3.1 Error Identification   | A     | ✅ Pass | Error: "Title is required..."          | Announced via `role="alert"` (Verified in Lab 1) |
| 3.3.2 Labels or Instructions | A     | ❌ Fail | Priority input missing label           | User does not know what to enter                 |
| 3.3.3 Error Suggestion       | AA    | ✅ Pass | Error message includes correction hint | "Please enter at least one character"            |

---

## Robust (Principle 4)

### 4.1 Compatible
| Criterion               | Level | Status | Evidence                                 | Notes                                         |
|-------------------------|-------|--------|------------------------------------------|-----------------------------------------------|
| 4.1.2 Name, Role, Value | A     | ❌ Fail | Footer link `<a href="/about"></a>`      | Empty link, no accessible name (Found by axe) |
| 4.1.3 Status Messages   | AA    | ✅ Pass | `<div role="status" aria-live="polite">` | "Task updated" announced correctly            |

---

## Summary
- **Total criteria evaluated**: 15
- **Pass**: 11
- **Fail**: 4 (1.1.1, 1.3.1, 1.4.3, 4.1.2)
- **N/A**: 0

---

## High-Priority Failures
1. **1.4.3 Contrast (Minimum)**: "Add Task" button has very low contrast (1.11:1).
   - **Action**: Modify CSS to darken button background or lighten text.

2. **1.3.1 / 3.3.2 Missing Label**: Priority input has no label.
   - **Action**: Add `aria-label="Priority (optional)"` to the input.

3. **1.1.1 Non-text Content**: Icon image missing alt text.
   - **Action**: Remove image or add `alt=""` if decorative.