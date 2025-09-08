use axum::extract::State;
use axum::Router;
use crate::AppState;

pub fn router(state: AppState) -> Router {
  Router::new()
      .route("/", axum::routing::get(get_assignments))
      .with_state(state)
}

async fn get_assignments(State(state): State<AppState>) -> &'static str {
  "List of assignments"
}