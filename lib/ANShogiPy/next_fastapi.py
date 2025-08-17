from fastapi import FastAPI
import uvicorn
import next
import tensorflow as tf
import numpy as np


# fast apiを起動する
app = FastAPI()


@app.get("/")
def top_root():
    return {"result": "hello world"}

#
# URLで呼ばれたら処理する
@app.get("/b/{item_id}")
def read_root(item_id: str):
    data = next.split_data(item_id)
    data = np.array([data])
    # print(data)
    result = next.model(data).numpy()[0][0].item()
    #print(result)
    return {"result": result}

if __name__ == "__main__":
    #
    uvicorn.run(app, host="127.0.0.1", port=8083, log_level="info")
    #

#