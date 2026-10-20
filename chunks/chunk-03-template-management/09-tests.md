# Unit Tests — Chunk 03: Template Management (`chunk-03-template-management`)

## 1. Unit Test Coverage
- **TemplateManagementViewModelTest**:
  - `onTemplateClick emits NavigateToDetail effect`
  - `onCreateTemplateClick emits NavigateToCreate effect`
  - `observeTemplates groups templates by context`
- **TemplateDetailViewModelTest**:
  - `loads template details successfully`
  - `onEditClick emits NavigateToEdit effect`
  - `delete template dialog flow deletes template and navigates back`
- **TemplateEditViewModelTest**:
  - `validation prevents saving with empty title or items`
  - `creates new template and navigates back`
  - `edits existing template and updates repository`

## 2. Test File Locations
- [`app/src/test/java/com/example/dontforget/feature/templates/FakeTemplateRepository.kt`](file:///D:/DontForget/app/src/test/java/com/example/dontforget/feature/templates/FakeTemplateRepository.kt)
- [`app/src/test/java/com/example/dontforget/feature/templates/TemplateManagementViewModelTest.kt`](file:///D:/DontForget/app/src/test/java/com/example/dontforget/feature/templates/TemplateManagementViewModelTest.kt)
- [`app/src/test/java/com/example/dontforget/feature/templates/TemplateDetailViewModelTest.kt`](file:///D:/DontForget/app/src/test/java/com/example/dontforget/feature/templates/TemplateDetailViewModelTest.kt)
- [`app/src/test/java/com/example/dontforget/feature/templates/TemplateEditViewModelTest.kt`](file:///D:/DontForget/app/src/test/java/com/example/dontforget/feature/templates/TemplateEditViewModelTest.kt)
