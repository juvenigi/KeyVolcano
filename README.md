# KeyVolcano frontend

## Simple idea

- Neutralinojs to serve React or god knows what
- Java: launches node, handles input events

## how it (should) work

ws client -> sends a request

if java accepts, the session is kept (observer pattern / subscription)
- java will continue sending keybind events
if java rejects, the session is terminated

### TODO

- [ ] handling busy ports
- [ ] consider easier way to establish conn
- [ ] security implications / reject anything which is not localhost
- [ ] write a shadcn-y frontend / app launcher shell?
- 