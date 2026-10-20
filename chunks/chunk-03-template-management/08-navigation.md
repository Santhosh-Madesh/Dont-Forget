# Navigation — Chunk 03: Template Management (`chunk-03-template-management`)

## Route & Navigation Graph Wiring

### 1. Route Definitions
- `Route.TemplateManagement` (`"template_management"`)
- `Route.TemplateDetail(templateId)` (`"template_detail/$templateId"`, ROUTE = `"template_detail/{templateId}"`)
- `Route.TemplateEdit(templateId)` (`"template_edit/$templateId"`, ROUTE = `"template_edit/{templateId}"`)

### 2. Destination Composables in `MainActivity.kt`
```kotlin
composable(Route.TemplateManagement.route) {
    val managementViewModel = remember { TemplateManagementViewModel(templateRepo) }
    TemplateManagementScreen(
        viewModel = managementViewModel,
        onNavigateToDetail = { templateId ->
            navController.navigate(Route.TemplateDetail.createRoute(templateId))
        },
        onNavigateToCreate = {
            navController.navigate(Route.TemplateEdit.createRoute(0L))
        }
    )
}

composable(
    route = Route.TemplateDetail.ROUTE,
    arguments = listOf(navArgument("templateId") { type = NavType.LongType })
) { backStackEntry ->
    val id = backStackEntry.arguments?.getLong("templateId") ?: 0L
    val detailViewModel = remember(id) { TemplateDetailViewModel(id, templateRepo) }
    TemplateDetailScreen(
        viewModel = detailViewModel,
        onNavigateToEdit = { templateId ->
            navController.navigate(Route.TemplateEdit.createRoute(templateId))
        },
        onNavigateBack = {
            navController.popBackStack()
        }
    )
}

composable(
    route = Route.TemplateEdit.ROUTE,
    arguments = listOf(navArgument("templateId") { type = NavType.LongType })
) { backStackEntry ->
    val id = backStackEntry.arguments?.getLong("templateId") ?: 0L
    val editViewModel = remember(id) { TemplateEditViewModel(id, templateRepo) }
    TemplateEditScreen(
        templateId = id,
        viewModel = editViewModel,
        onNavigateBack = {
            navController.popBackStack()
        }
    )
}
```
