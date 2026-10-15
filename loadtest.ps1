param([int]$BookId=1,[int]$Requests=50,[int[]]$Ports=@(18026,18126,18226))
 $jobs=@()
 for($i=0;$i -lt $Requests;$i++) {
   $port=$Ports[$i % $Ports.Count]
   $jobs+=Start-Job -ArgumentList $port,$BookId -ScriptBlock {
     param($Port,$Id)
     try { $r=Invoke-WebRequest -UseBasicParsing -Method Post -Uri "http://localhost:$Port/api/books/$Id/borrow"; [PSCustomObject]@{Port=$Port;Status=[int]$r.StatusCode;Body=$r.Content} }
     catch { $status=0; if($_.Exception.Response){$status=[int]$_.Exception.Response.StatusCode};[PSCustomObject]@{Port=$Port;Status=$status;Body=$_.ErrorDetails.Message} }
   }
 }
 $rows=@($jobs|Wait-Job|Receive-Job);$jobs|Remove-Job
 $success=@($rows|Where-Object Status -eq 200).Count
 $conflict=@($rows|Where-Object Status -eq 409).Count
 $other=$Requests-$success-$conflict
 "Success (200): $success";"Conflict (409): $conflict";"Other: $other"
 $rows|ConvertTo-Json -Depth 5
 if($other -gt 0){exit 1}
