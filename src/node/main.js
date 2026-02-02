import { spawn } from "node:child_process";

// const term = spawn('alacritty', ['--command', 'bash'], {
//     stdio: 'pipe' // retain stdin/stdout
// });
//
// // Programmatically send commands
// term.stdin.write('echo Hello Linux\n');
//
// // Read output
// term.stdout.on('data', data => {
//     console.log(`STDOUT: ${data}`);
// });


// --- 2. PowerShell (Windows) ---
// const psTerm = spawn("powershell.exe", [], {
//     stdio: "pipe",
//     windowsHide: false, // make it visible
// });
//
// psTerm.stdin.write("Write-Host 'Hello PowerShell'\n");
//
// psTerm.stdout.on("data", (data) => {
//     console.log(`[PowerShell STDOUT]: ${data.toString()}`);
// });
//
// psTerm.stderr.on("data", (data) => {
//     console.error(`[PowerShell STDERR]: ${data.toString()}`);
// });
//
// psTerm.on("exit", (code, signal) => {
//     console.log(`[PowerShell] exited with code ${code}, signal ${signal}`);
// });

import { randomUUID } from "crypto";
import net from "node:net";


const pipeIn = "\\\\.\\pipe\\launcher_in_1234";   // Node → Terminal
const pipeOut = "\\\\.\\pipe\\launcher_out_1234"; // Terminal → Node


const term = spawn("powershell.exe", ["-File", "bootstrap.ps1", pipeIn, pipeOut], {
    stdio: "ignore", // Node communicates via named pipe, not stdio
    detached: true,
});

const clientIn = net.createConnection(pipeIn, () => {
    console.log("Connected to PowerShell in pipe!");
    clientIn.write("echo Hello from Node!\n");
});

const clientOut = net.createConnection(pipeOut, () => console.log("Connected to terminal output"));
clientOut.on("data", data => console.log(`[Terminal]: ${data.toString()}`));

// Handle data from terminal
// client.on("data", (data) => {
//     console.log(`From terminal: ${data.toString()}`);
// });
//
// // Handle pipe end / close
// client.on("end", () => {
//     console.log("Pipe closed by terminal");
// });
//
// client.on("error", (err) => {
//     console.error("Pipe error:", err);
// });