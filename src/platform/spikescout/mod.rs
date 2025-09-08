mod routes;
mod types;
mod repo;
pub mod errors;

use axum::Router;
use crate::AppState;

pub fn app(state: AppState) -> Router {
  Router::new()
      // .nest("/auth", auth::router(state))
      .with_state(state)
}