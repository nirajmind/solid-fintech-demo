Write-Host "🚀 Starting Kubernetes Port Forwards..." -ForegroundColor Green

# 1. Solid App (Local 8080 -> Service 8080)
Write-Host "Opening Solid App on http://localhost:8080"
Start-Process powershell -ArgumentList "-NoExit", "-Command", "kubectl port-forward service/solid-app-service 8080:8080"

# 2. Jenkins (Local 8081 -> Service 8080)
# We map to 8081 so it doesn't conflict with your App
Write-Host "Opening Jenkins on http://localhost:8081"
Start-Process powershell -ArgumentList "-NoExit", "-Command", "kubectl port-forward service/jenkins-service 8081:8080"

# 3. SonarQube (Local 9000 -> Service 9000)
Write-Host "Opening SonarQube on http://localhost:9000"
Start-Process powershell -ArgumentList "-NoExit", "-Command", "kubectl port-forward service/sonar-service 9000:9000"

# 4. MongoDB (Local 27017 -> Service 27017)
# Useful if you want to connect using a tool like MongoDB Compass
Write-Host "Opening MongoDB on localhost:27017"
Start-Process powershell -ArgumentList "-NoExit", "-Command", "kubectl port-forward service/mongo-service 27017:27017"

Write-Host "✅ Tunnels started in separate windows. Do not close them!" -ForegroundColor Yellow