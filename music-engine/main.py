from fastapi import FastAPI

from service.midi_to_mp3_service import MidiToMp3Service

app = FastAPI()


@app.get("/")
async def root():
    return {"message": "Hello World"}


@app.get("/hello/{name}")
async def say_hello(name: str):
    return {"message": f"Hello {name}"}


@app.get("/convert/{filename}")
async def say_hello(filename: str):
    midi_to_mp3_service = MidiToMp3Service()
    midi_to_mp3_service.convert_midi_to_mp3()
    return {"message": f"{filename} 생성 완료"}
