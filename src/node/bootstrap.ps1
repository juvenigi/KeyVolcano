param(
    [string]$PipeIn,
    [string]$PipeOut
)

# Connect to Node pipes
$inPipe  = new-object System.IO.Pipes.NamedPipeClientStream(".", $PipeIn, [System.IO.Pipes.PipeDirection]::In)
$outPipe = new-object System.IO.Pipes.NamedPipeClientStream(".", $PipeOut, [System.IO.Pipes.PipeDirection]::Out)
$inPipe.Connect()
$outPipe.Connect()

# --- Node → Terminal ---
Start-Job {
    $reader = new-object System.IO.StreamReader($inPipe)
    while ($true) {
        $line = $reader.ReadLine()
        if ($line) {
            # Instead of replacing stdin, "type" the command into the console
            # For simplicity, just write to host with new line
            Write-Host $line
        }
    }
}

# --- Terminal → Node ---
# Capture output asynchronously and send to Node
Start-Job {
    $outWriter = new-object System.IO.StreamWriter($outPipe)
    $outWriter.AutoFlush = $true
    while ($true) {
        # You can poll the console buffer or capture host output
        # Example: just read lines user types and echo them
        $line = Read-Host
        if ($line) { $outWriter.WriteLine($line) }
    }
}
