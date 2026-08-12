# wordgame backend

## Local environment

Copy `.env.example` to `.env` and set the Korean dictionary API key.

```properties
KOREAN_DICTIONARY_API_KEY=your-api-key
```

Spring Boot loads `.env` when the application is started from either the
repository root or the `backend` directory. The real `.env` file is excluded
from Git.
