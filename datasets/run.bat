copy /Y dataAnalyzerForNumbers.jar Statefair/dataAnalyzerForNumbers.jar
copy /Y dataAnalyzerForNumbers.jar NCSU/dataAnalyzerForNumbers.jar
copy /Y dataAnalyzerForNumbers.jar NewYork/dataAnalyzerForNumbers.jar
copy /Y dataAnalyzerForNumbers.jar Orlando/dataAnalyzerForNumbers.jar
copy /Y dataAnalyzerForNumbers.jar KAIST/dataAnalyzerForNumbers.jar
cd KAIST
rmdir /Q /S processedData
run.bat
cd..
cd Orlando
rmdir /Q /S processedData
run.bat
cd..
cd Statefair
rmdir /Q /S processedData
run.bat
cd..
cd NCSU
rmdir /Q /S processedData
run.bat
cd..
cd NewYork
rmdir /Q /S processedData
run.bat
cd..
