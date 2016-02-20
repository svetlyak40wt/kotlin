# CHANGELOG

## 1.1

## 1.0.2

### JS
- Safe calls (`x?.let { it }`) are now inlined

### Tools. J2K
- Protected members used outside of inheritors are converted as public
- Support conversion for annotation constructor calls
- Place comments from the middle of the call to the end
- Drop line breaks between operator arguments (except '+', "-", "&&" and "||")
- Add non-null assertions on call site for non-null parameters

### IDE

New features:

- [KT-11098](https://youtrack.jetbrains.com/issue/KT-11098) Inspection on final classes/functions annotated with Spring @Configuration/@Component/@Bean