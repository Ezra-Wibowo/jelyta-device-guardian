# Deployment Guide

## Production Cloud Backend Deployment

### Docker Deployment
```bash
docker-compose -f docker-compose.yml up -d --build
```

### Nginx Reverse Proxy Setup
```nginx
server {
    listen 80;
    server_name api.deviceguardian.jelyta.com;

    location / {
        proxy_pass http://127.0.0.1:8000;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
    }
}
```

### Environment Configuration
Set the following environment variables in `.env`:
- `GEMINI_API_KEY`: API key for Google Gemini model access.
- `ENVIRONMENT`: Set to `production`.
