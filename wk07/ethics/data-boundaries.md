# Data Boundaries — Week 7

## What We Collect (Allowed)
- **Pseudonymised session IDs**: Random 6-char hex (e.g., `P1_a3f7`)
- **Task metadata**: Title, completion status, timestamps
- **Interaction logs**: HTTP requests, response times, error codes
- **Accessibility testing notes**: "NVDA announced X", "Focus moved to Y"

## What We DO NOT Collect (Prohibited)
- ❌ Real names (use pseudonyms: Participant A, P1, etc.)
- ❌ Student ID numbers
- ❌ Email addresses
- ❌ IP addresses (Ktor logs disabled in production)
- ❌ Content of tasks beyond module examples (e.g., no personal to-do items)

## Storage & Retention
- **Location**: Local CSV files (`data/tasks.csv`, `data/metrics.csv`)
- **Access**: Researcher, lab partner, module staff (on request)
- **Encryption**: Standard filesystem permissions (chmod 600)
- **Deletion**: End of Semester 1 (January 2025) OR anonymised for portfolio

## Privacy by Design Principles Applied
1. **Data minimisation**: Only collect session ID (not name)
2. **Purpose limitation**: Metrics used only for HCI evaluation (not sold/shared)
3. **Storage limitation**: Delete after assessment complete
4. **Integrity & confidentiality**: Local storage (not cloud), Git repo private

## Ethics Risks Identified
| Risk                               | Mitigation                                                           |
|------------------------------------|----------------------------------------------------------------------|
| Task titles contain sensitive info | Provide example tasks ("Buy milk"); warn against personal content    |
| Session IDs linked to participants | Use randomised IDs; don't store mapping                              |
| Git repo accidentally public       | Verify `.git/config` remote is private; add `.gitignore` for `/data` |
| Screenshots include PII            | Crop to relevant UI; blur names if visible                           |

---

**Reference**: ICO (2024). Guide to GDPR, <https://ico.org.uk/for-organisations/uk-gdpr-guidance-and-resources/>
