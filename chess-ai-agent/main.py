from dotenv import load_dotenv
load_dotenv()

import uvicorn
import logging

logging.getLogger("uvicorn.error").addFilter(
    lambda record: "upgrade request" not in record.getMessage().lower() 
    and "websocket" not in record.getMessage().lower()
)

if __name__ == "__main__":
    uvicorn.run("agent:app", host="0.0.0.0", port=8000, reload=True)