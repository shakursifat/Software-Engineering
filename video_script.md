# 🎬 YouTube Video Script — JMeter Assignment Demo
**Estimated Video Length: 10–15 minutes**

---

## 🎙️ BEFORE YOU START RECORDING

- Open JMeter (test plan already built and saved)
- Open your browser at `http://103.94.135.91:8080`
- Have PowerShell ready
- Turn on your webcam in OBS or Zoom
- Keep this script visible on your phone or a second monitor

---

---

## SECTION 1 — Introduction (0:00 – 0:45)

> **[Look at webcam, speak clearly]**

---

**SAY:**
> "Assalamu Alaikum / Hello everyone. My name is [Your Name], and my student ID is [Your ID].
>
> In this video, I will demonstrate my complete workflow for the Performance and Load Testing assignment using Apache JMeter.
>
> The target application I will be testing is the LoadLab web application, hosted at http://103.94.135.91 on port 8080.
>
> My test plan covers five distinct HTTP endpoints, and I will run two separate load profiles — one with 50 concurrent threads and one with 100 concurrent threads — with a ramp-up period of 100 seconds each.
>
> I will also show you the Duration Assertion configuration, live test execution results, and the automatically generated HTML dashboard report.
>
> Let's get started."

---

---

## SECTION 2 — Show the Target Application (0:45 – 1:30)

> **[Switch to browser, show the website]**

---

**SAY:**
> "First, let me quickly show you the LoadLab application we are testing.
>
> This is the Home Page — a simple GET request to the root path.
>
> Here is the Notice Board — this page has a random delay of 100 to 400 milliseconds built in.
>
> Here is the Course Catalogue — this is a CPU-intensive page that degrades under high user load.
>
> The Login page accepts a POST request with username 'student' and password 'student123', and it returns a 303 redirect with a session cookie.
>
> And finally, the Data API endpoint at /api/download/256 is a high-throughput endpoint designed to measure data transfer rates in kilobytes per second.
>
> These five endpoints cover a diverse range of HTTP behaviors — which is why they were chosen for this assignment."

---

---

## SECTION 3 — Open JMeter & Show Test Plan Structure (1:30 – 3:00)

> **[Switch to JMeter GUI]**

---

**SAY:**
> "Now let me open Apache JMeter version 5.6.3.
>
> You can see my Test Plan here, named 'LoadLab Performance Test'.
>
> Let me walk you through the structure."

> **[Click on HTTP Request Defaults in the tree]**

**SAY:**
> "I have added an HTTP Request Defaults configuration element at the Test Plan level. This sets the shared server IP to 103.94.135.91 and the port to 8080 — so I don't have to repeat these values in every individual HTTP sampler."

> **[Expand the Low Load Thread Group]**

**SAY:**
> "I have two Thread Groups in this plan.
>
> The first is the Low Load profile — configured with 50 threads, a ramp-up period of 100 seconds, and a loop count of 1. This simulates 50 concurrent users gradually increasing over 100 seconds."

> **[Click on each Recording Controller one by one]**

**SAY:**
> "Inside this Thread Group, I have organized the five target endpoints into separate Recording Controllers as required by the assignment.
>
> Recording Controller 1 — Home Page
> Recording Controller 2 — Notice Board
> Recording Controller 3 — Course Catalogue
> Recording Controller 4 — User Login
> Recording Controller 5 — Data API
>
> Each Recording Controller contains the corresponding HTTP sampler."

---

---

## SECTION 4 — Show Each HTTP Sampler (3:00 – 4:30)

> **[Click on each HTTP Request sampler]**

---

**SAY:**
> "Let me open the Home Page sampler. You can see the method is GET, and the path is forward slash — which targets the root URL."

> **[Click GET /notices]**

**SAY:**
> "The Notice Board sampler is also a GET request, targeting the /notices path."

> **[Click GET /courses]**

**SAY:**
> "Course Catalogue — GET request to /courses."

> **[Click POST /login]**

**SAY:**
> "The User Login sampler uses the POST method, targeting /login.
>
> In the Parameters section, I have added two form fields — username set to 'student', and password set to 'student123'. These are sent as URL-encoded form data, which is the standard format for HTML login forms."

> **[Click GET /api/download/256]**

**SAY:**
> "And the Data API sampler is a GET request to /api/download/256. This endpoint sends a 256-kilobyte payload, allowing us to measure data reception rates."

---

---

## SECTION 5 — Show HTTP(S) Test Script Recorder (4:30 – 5:15)

> **[Click on the HTTP(S) Test Script Recorder under Non-Test Elements]**

---

**SAY:**
> "As required by the assignment, I have also added the HTTP(S) Test Script Recorder under Non-Test Elements.
>
> This element acts as a proxy recorder — it listens on port 8888, intercepts real browser traffic, and automatically generates HTTP request samples in JMeter.
>
> The Target Controller is set to capture requests into the appropriate Recording Controller.
>
> For this assignment, I have added the HTTP request samples manually since the target server is plain HTTP and I have already identified all five endpoints. The recorder is configured and available here as required."

---

---

## SECTION 6 — Show Duration Assertion (5:15 – 6:00)

> **[Click on Duration Assertion inside the Thread Group]**

---

**SAY:**
> "Now let me show you the Duration Assertion — this is one of the key requirements of the assignment.
>
> I have set the maximum allowed duration to 200 milliseconds.
>
> Here is the reasoning behind this threshold:
> - The Home Page responds in 0 to 30 milliseconds — well under 200ms, so assertions will PASS.
> - The Notice Board has a random delay of 100 to 400 milliseconds — often above 200ms, so assertions will FAIL.
> - The Course Catalogue is CPU-intensive with 150 to 250 milliseconds — borderline, causing mixed results.
>
> This intentional threshold ensures that we observe BOTH passing and failing assertion events during the test, which demonstrates that the assertion is working correctly.
>
> The same Duration Assertion is applied to the High Load Thread Group as well."

---

---

## SECTION 7 — Show Listeners (6:00 – 6:30)

> **[Click on each Listener]**

---

**SAY:**
> "I have attached three listeners to each Thread Group as required:
>
> First — View Results Tree. This gives a live, color-coded view of every sample, showing green for passed requests and red for failed ones.
>
> Second — Aggregate Report. This provides a statistical summary including minimum, maximum, average response times, throughput in requests per second, and error percentage for each endpoint.
>
> Third — Summary Report. This gives an overall summary of the entire test run.
>
> I have also configured the Aggregate Report to save results directly to a CSV file for submission."

---

---

## SECTION 8 — Run the 50-Thread Test (6:30 – 8:30)

> **[Make sure High Load Thread Group is DISABLED. Click Run → Start]**

---

**SAY:**
> "I will now execute the first test run — the Low Load profile with 50 concurrent threads.
>
> The High Load Thread Group is currently disabled so only the 50-thread group will run.
>
> I'll click the Start button now."

> **[Switch to View Results Tree, let results come in]**

**SAY:**
> "You can see the View Results Tree populating in real time.
>
> The green entries are requests that passed — both the HTTP response was successful AND the Duration Assertion was satisfied within 200 milliseconds.
>
> The red entries are requests where the Duration Assertion failed — the server took longer than 200 milliseconds to respond, exceeding our threshold.
>
> This is exactly the behavior we expected — the Home Page is fast and passes, while the Notice Board and Course Catalogue exceed the threshold."

> **[Click on a red entry, show the Response Data tab]**

**SAY:**
> "If I click on a failed entry, I can see in the Response tab that the assertion failure message states: 'Response was too slow — took X milliseconds, the limit is 200 milliseconds.'
>
> This confirms the Duration Assertion is working correctly."

> **[Switch to Aggregate Report after test finishes]**

**SAY:**
> "The test has completed. Let me switch to the Aggregate Report.
>
> Here you can see all five endpoints listed with their statistics:
> - Samples: 50 for each endpoint
> - Minimum, Maximum, and Average response times in milliseconds
> - Throughput in requests per second
> - Error percentage caused by the Duration Assertion failures
>
> I will now save this data to a CSV file."

> **[Right-click Aggregate Report → Save Table Data]**

---

---

## SECTION 9 — Run the 100-Thread Test (8:30 – 10:00)

> **[Clear all results: Run → Clear All. Disable 50-thread group, Enable 100-thread group. Click Run]**

---

**SAY:**
> "Now I will clear all previous results and run the High Load profile — 100 concurrent threads.
>
> I have disabled the 50-thread group and enabled the 100-thread group.
>
> Starting the test now."

> **[Watch View Results Tree]**

**SAY:**
> "Notice that under higher load, we see more red entries appearing. This is because with 100 concurrent users, the server is under greater stress.
>
> The CPU-intensive Course Catalogue endpoint is particularly affected — the response times increase significantly under load, causing more Duration Assertion failures.
>
> The error percentage increases compared to the 50-thread run, which we will analyze in our report."

> **[Show Aggregate Report after completion]**

**SAY:**
> "The 100-thread test is complete.
>
> Comparing these results to the 50-thread run:
> - Average response times are higher across all endpoints
> - Throughput has increased in absolute terms but efficiency may have dropped
> - The error rate is higher due to more assertion failures under load
>
> I'm saving this report to CSV as well."

---

---

## SECTION 10 — Generate HTML Dashboard Report (10:00 – 11:30)

> **[Switch to PowerShell]**

---

**SAY:**
> "Now I will generate the JMeter HTML Dashboard Report using the command line.
>
> I'll open PowerShell and navigate to the JMeter bin directory."

> **[Type and run the command]**

```powershell
cd "C:\Users\Sifat\Downloads\apache-jmeter-5.6.3\apache-jmeter-5.6.3\bin"
.\jmeter.bat -g "C:\Users\Sifat\Downloads\yourID_result_100.csv" -o "C:\Users\Sifat\Downloads\yourID_html_report"
```

**SAY:**
> "The -g flag specifies the input CSV file from our test results.
> The -o flag specifies the output folder where the HTML report will be generated.
>
> JMeter is now processing the data and creating the dashboard."

> **[Wait for it to finish, then open the index.html in browser]**

**SAY:**
> "The HTML report has been generated. Let me open it in the browser."

> **[Show the HTML Dashboard — charts, graphs, statistics]**

**SAY:**
> "This is the JMeter HTML Dashboard Report. It contains:
> - Response Times Over Time chart
> - Transactions per Second graph
> - Response Time Percentiles
> - A statistics table with all performance metrics
>
> This dashboard is automatically generated from our test results and provides a professional visual summary of the entire performance test.
>
> I will compress this folder into a zip file for submission."

---

---

## SECTION 11 — Show Submission Files (11:30 – 12:00)

> **[Open File Explorer, show all files]**

---

**SAY:**
> "Let me quickly show all the submission artifacts I have prepared:
>
> 1. The JMX test plan file — this is the exported JMeter configuration
> 2. The CSV results file — raw aggregate data from the test runs
> 3. The HTML zip — the compressed dashboard report
> 4. The assessment report document — containing the performance analysis tables and discussion
>
> All files are named with my student ID as required."

---

---

## SECTION 12 — Closing (12:00 – 12:30)

> **[Look at webcam]**

---

**SAY:**
> "That concludes my demonstration of Performance and Load Testing using Apache JMeter.
>
> To summarize what I covered:
> - I built a test plan targeting five distinct HTTP endpoints on the LoadLab application
> - I configured two load profiles — 50 and 100 concurrent threads — with a 100-second ramp-up
> - I applied a 200-millisecond Duration Assertion that produced both passing and failing results
> - I analyzed the results using View Results Tree and Aggregate Report
> - And I generated the HTML Dashboard Report for visual analysis
>
> Thank you for watching. If you have any questions, feel free to leave a comment below."

---

---

## 📋 Checklist Before Uploading to YouTube

- [ ] Your face is visible in webcam overlay throughout the video
- [ ] You said your name and student ID at the beginning
- [ ] Showed the target application in the browser
- [ ] Showed HTTP Request Defaults, both Thread Groups (50 & 100)
- [ ] Showed all 5 Recording Controllers and HTTP Samplers
- [ ] Showed HTTP(S) Test Script Recorder under Non-Test Elements
- [ ] Showed Duration Assertion with 200ms threshold and explained why
- [ ] Showed View Results Tree with green AND red results
- [ ] Showed Aggregate Report with populated data
- [ ] Ran BOTH 50-thread and 100-thread tests
- [ ] Generated HTML report via PowerShell command
- [ ] Showed HTML Dashboard in browser
- [ ] Showed all submission files
- [ ] Clear audio throughout

---

## ⏱️ Suggested Timeline

| Time | Section |
|---|---|
| 0:00 – 0:45 | Introduction + your name/ID |
| 0:45 – 1:30 | Show LoadLab website |
| 1:30 – 3:00 | JMeter Test Plan structure |
| 3:00 – 4:30 | Each HTTP Sampler |
| 4:30 – 5:15 | HTTP(S) Script Recorder |
| 5:15 – 6:00 | Duration Assertion explanation |
| 6:00 – 6:30 | Listeners |
| 6:30 – 8:30 | 50-thread test run |
| 8:30 – 10:00 | 100-thread test run |
| 10:00 – 11:30 | HTML Dashboard generation |
| 11:30 – 12:00 | Show submission files |
| 12:00 – 12:30 | Closing summary |
