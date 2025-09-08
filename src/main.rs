mod platform;

use std::net::SocketAddr;
use sqlx::PgPool;
use axum::{middleware, Router};
use tokio::net::TcpListener;
use crate::platform::core::middleware::auth;

#[derive(Clone)]
struct AppState {
    pg: PgPool,
    redis: deadpool_redis::Pool,
}

#[tokio::main]
async fn main() -> anyhow::Result<()> {
    // load env
    dotenvy::dotenv()?;

    // load config
    let settings = config::Config::builder()
        .add_source(config::Environment::default())
        .build()?;

    let pg_url = settings.get_string("DATABASE_URL")?;
    let redis_url = settings.get_string("REDIS_URL")?;

    let pg_pool = PgPool::connect(&pg_url).await?;

    let redis_cfg = deadpool_redis::Config::from_url(redis_url);
    let redis = redis_cfg.create_pool(Some(deadpool_redis::Runtime::Tokio1))?;

    let state = AppState {
        pg: pg_pool,
        redis
    };

    let app = Router::new()
        .nest("/api/v1", platform::app(state.clone()))
        .route_layer(middleware::from_fn(auth));

    let addr: SocketAddr = "0.0.0.0:3000".parse()?;
    axum::serve(TcpListener::bind(addr).await?, app).await?;

    println!("Server running on {}", addr);

    Ok(())
}
