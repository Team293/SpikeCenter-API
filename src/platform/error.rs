use axum::response::{IntoResponse, Response};
use thiserror::Error;
use crate::platform::spikescout::errors::SpikeScoutError;

#[derive(Debug, Error)]
pub enum ApplicationError {
  #[error(transparent)]
  SpikeScout(#[from] SpikeScoutError),

  #[error("database error: {0}")]
  Database(#[from] sqlx::Error)
}

impl IntoResponse for ApplicationError {
  fn into_response(self) -> Response {
    match self {
      ApplicationError::SpikeScout(err) => err.into_response(),

      ApplicationError::Database(err) =>
        (axum::http::StatusCode::INTERNAL_SERVER_ERROR, format!("database error {err}")).into_response()
    }
  }
}