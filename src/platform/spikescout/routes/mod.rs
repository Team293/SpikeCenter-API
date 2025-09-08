mod assignments;

use axum::Router;

pub fn router(state: crate::AppState) -> Router {
    Router::new()
        .nest("/assignments", assignments::router(state))
}