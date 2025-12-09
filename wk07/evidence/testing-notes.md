# Testing Notes — Week 7 Lab 1

**Date**: 2025-12-09
**Browser**: Microsoft Edge
**Tester**: Ningsong Jia

## 1. Dual-Path Verification

| Feature         | HTMX (JS On)                         | No-JS (JS Off)                 | Result   |
|:----------------|:-------------------------------------|:-------------------------------|:---------|
| **Inline Edit** | Form appears in-place without reload | Full page reload, form renders | **Pass** |
| **Save**        | Updates in-place + Status msg shown  | Redirects to list (PRG)        | **Pass** |
| **Validation**  | Error shown in-place (red text)      | Page reloads with error param  | **Pass** |
| **Cancel**      | Reverts to view mode                 | Redirects to list              | **Pass** |

## 2. Accessibility Verification

### Keyboard Navigation
* [x] **Tab Order**: Tabbed through Edit -> Input -> Save/Cancel without trapping.
* [x] **Focus Management**: Focus moved automatically to the input field when "Edit" was clicked.
* [x] **Visual Focus**: Focus indicators were visible on all interactive elements.

### Screen Reader (NVDA/VoiceOver)
* [x] **Error Announcement**: Confirmed that the validation error ("Title is required") was announced immediately due to `role="alert"`.
* [x] **Status Update**: Confirmed "Task updated" was announced after saving.
* [x] **Labels**: All form inputs have associated labels (visible or visually-hidden).

## 3. Evidence Files Reference

I have captured the following screenshots as evidence of the testing above:

* `01-view-mode-htmx.png`: Shows the initial state of the task list.
* `02-edit-mode-htmx.png`: Shows the inline edit form with focus on the input.
* `03-validation-error-htmx.png`: Shows the server-side validation error with `role="alert"` visible in DevTools.
* `04-status-oob-devtools.png`: Shows the "Task updated" status message injected into the DOM.
* `05-edit-mode-nojs.png`: Shows the full-page reload behavior when JavaScript is disabled.
* `06-nvda-error-announcement.png`: Shows the NVDA Speech Viewer log confirming the error was read aloud.

## 4. Issues Found & Fixed
* **Issue**: Initial validation returned 400 Bad Request, causing HTMX to halt swapping.
* **Fix**: Updated `TaskRoutes.kt` to return 200 OK with the error HTML fragment, allowing HTMX to render the error message properly.

## 5. WCAG Compliance Check
* **WCAG 3.3.1 (Error Identification)**: Pass - Errors are identified in text.
* **WCAG 4.1.3 (Status Messages)**: Pass - Status changes are programmatically determined via ARIA live regions.
* **WCAG 2.1.1 (Keyboard)**: Pass - All functionality is operable through a keyboard interface.