FROM mcr.microsoft.com/dotnet/aspnet:8.0
COPY dashboard/. .
ARG JENKINS_HOME=/Jenkins
ENTRYPOINT ["dotnet","Lightwell-Testing-Dashboard-2.dll"]