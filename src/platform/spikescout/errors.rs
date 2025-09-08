use axum::response::IntoResponse;
use thiserror::Error;

#[derive(Debug, Error)]
pub enum SpikeScoutError {}

impl IntoResponse for SpikeScoutError {
  fn into_response(self) -> axum::response::Response {
    match self {

    }
  }
}