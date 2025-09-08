use crate::platform::core::types::CurrentUser;
use axum::http::header::AUTHORIZATION;
use axum::{
  extract::Request,
  http::StatusCode,
  middleware::Next,
  response::Response,
};

pub async fn auth(mut req: Request, next: Next) -> Result<Response, StatusCode> {
  let auth_header = req.headers()
      .get(AUTHORIZATION)
      .and_then(|value| value.to_str().ok());

  let auth_header = if let Some(header) = auth_header {
    header
  } else {
    return Err(StatusCode::UNAUTHORIZED);
  };

  if let Some(user) = authorize_user(auth_header).await {
    req.extensions_mut().insert(user);
    Ok(next.run(req).await)
  } else {
    Err(StatusCode::UNAUTHORIZED)
  }
}

async fn authorize_user(header: &str) -> Option<CurrentUser> {
  todo!()
}