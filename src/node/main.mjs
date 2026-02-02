import { spawn } from "node:child_process";
import { randomUUID } from "crypto";
import net from "node:net";

// Generate unique pipe names (optional: use UUID)
const pipeId = randomUUID();
const pipeIn = `\\\\.\\pipe\\launcher_in_${pipeId}`;   // Node → Terminal
const pipeOut = `\\\\.\\pipe\\launcher_out_${pipeId}`; // Terminal → Node

// --- Spawn PowerShell bootstrap ---
const term = spawn(
    "cmd.exe",
    [
        "/c",
        "start", // create a new visible window
        "powershell.exe",
        "-NoExit",
        "-File",
        ".\\bootstrap.ps1",
        pipeIn,
        pipeOut
    ],
    {
        stdio: "ignore",  // communication happens via named pipes
        detached: true,   // window survives if Node exits
        windowsHide: false
    }
);

console.log('spawned', term.pid)

// --- Helper: retryable pipe connection ---
async function connectPipe(pipeName, retries = 50, delay = 50) {
    return new Promise((resolve, reject) => {
        let attempt = 0;

        function tryConnect() {
            const client = net.createConnection(pipeName);
            client.once("connect", () => resolve(client));
            client.once("error", (err) => {
                if (err.code === "ENOENT" && attempt < retries) {
                    attempt++;
                    setTimeout(tryConnect, delay);
                } else {
                    reject(err);
                }
            });
        }

        tryConnect();
    });
}

async function main() {
    try {
        // Connect to PowerShell stdin pipe
        const clientIn = await connectPipe(pipeIn);
        console.log("Connected to PowerShell input pipe!");

        // Connect to PowerShell stdout pipe
        const clientOut = await connectPipe(pipeOut);
        console.log("Connected to terminal output pipe!");

        // Handle output from terminal
        clientOut.on("data", (data) => {
            console.log(`[Terminal]: ${data.toString()}`);
        });

        // Send a test command
        clientIn.write("Write-Host 'Hello from Node!'\n");

        // Example: send more commands after 1 second
        setTimeout(() => {
            clientIn.write("Write-Host 'Another command from Node'\n");
        }, 1000);

    } catch (err) {
        console.error("Failed to connect to pipe:", err);
    }
}

// Run
main();
