# CHANGELOG

## 1.1

## 1.0.2
- Do not show 'Kotlin not configured' during project gradle sync
- Show only changed files in notification "Kotlin not configured"

### JS
- Safe calls (`x?.let { it }`) are now inlined

### Tools. J2K
- Protected members used outside of inheritors are converted as public
- Support conversion for annotation constructor calls
- Place comments from the middle of the call to the end
- Drop line breaks between operator arguments (except '+', "-", "&&" and "||")
- Add non-null assertions on call site for non-null parameters