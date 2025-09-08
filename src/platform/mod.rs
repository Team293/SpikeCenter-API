mod spikescout;
pub mod core;
mod error;

use axum::Router;
use crate::AppState;

pub fn app(state: AppState) -> Router {
  Router::new()
      .nest("/spikescout", spikescout::app(state.clone()))
}