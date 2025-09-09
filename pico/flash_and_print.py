import uasyncio as aio
from machine import Pin

led = Pin("LED", Pin.OUT)

async def blink():
    while True:
        led.toggle()
        await aio.sleep(0.03)

async def say_hello():
    while True:
        print("Hello!")
        await aio.sleep(1)

aio.run(aio.gather(blink(), say_hello()))
