# JMeter Assignment – Complete Step-by-Step Guide
**Deadline: Tonight, 11:59 PM**

---

## ⚡ Quick Overview of What You Need to Do

1. Build a JMeter Test Plan (JMX file)
2. Run two test profiles: 50 threads & 100 threads
3. Export CSV results & generate HTML Dashboard
4. Fill in the report table
5. Record a YouTube video with webcam overlay

---

## PHASE 1 — Launch JMeter

1. Open **File Explorer** → navigate to:
   `C:\Users\Sifat\Downloads\apache-jmeter-5.6.3\apache-jmeter-5.6.3\bin`
2. Double-click **`jmeter.bat`** to launch the GUI.
3. JMeter opens with a blank Test Plan.

---

## PHASE 2 — Build the Test Plan (GUI)

### Step 1: Rename the Test Plan
- Click **Test Plan** in the left tree panel.
- In the right panel, rename it to: `LoadLab Performance Test`
- Check **"Functional Test Mode"** → OFF (leave unchecked).

---

### Step 2: Add the HTTP Request Defaults (shared config)
Right-click **Test Plan** → **Add** → **Config Element** → **HTTP Request Defaults**

Set the following:
| Field | Value |
|---|---|
| Protocol | `http` |
| Server Name or IP | `103.94.135.91` |
| Port Number | `8080` |

---

### Step 3: Add Thread Group for Low Load (50 threads)
Right-click **Test Plan** → **Add** → **Threads (Users)** → **Thread Group**

Configure it:
| Field | Value |
|---|---|
| Name | `Low Load - 50 Threads` |
| Number of Threads | `50` |
| Ramp-Up Period (seconds) | `100` |
| Loop Count | `1` |

---

### Step 4: Add 5 Recording Controllers inside the Thread Group
Right-click **Low Load - 50 Threads** → **Add** → **Logic Controller** → **Recording Controller**

Repeat this 5 times, naming them:
1. `RC - Home Page`
2. `RC - Notice Board`
3. `RC - Course Catalogue`
4. `RC - User Login`
5. `RC - Data API`

---

### Step 5: Add HTTP Requests manually to each Recording Controller

> **NOTE:** Instead of using the browser proxy recorder (which requires SSL certificate setup), you will **manually add HTTP Samplers** to each Recording Controller. This is the most reliable method.

#### 5a. Home Page — GET /
Right-click **RC - Home Page** → **Add** → **Sampler** → **HTTP Request**
| Field | Value |
|---|---|
| Name | `GET Home Page` |
| Method | `GET` |
| Path | `/` |

#### 5b. Notice Board — GET /notices
Right-click **RC - Notice Board** → **Add** → **Sampler** → **HTTP Request**
| Field | Value |
|---|---|
| Name | `GET Notice Board` |
| Method | `GET` |
| Path | `/notices` |

#### 5c. Course Catalogue — GET /courses
Right-click **RC - Course Catalogue** → **Add** → **Sampler** → **HTTP Request**
| Field | Value |
|---|---|
| Name | `GET Course Catalogue` |
| Method | `GET` |
| Path | `/courses` |

#### 5d. User Login — POST /login
Right-click **RC - User Login** → **Add** → **Sampler** → **HTTP Request**
| Field | Value |
|---|---|
| Name | `POST User Login` |
| Method | `POST` |
| Path | `/login` |
| Content Encoding | (leave blank) |

Scroll down to **Parameters** section → click **Add** twice:
| Name | Value |
|---|---|
| `username` | `student` |
| `password` | `student123` |

Also check **"Use multipart/form-data"** → NO. The content type should be `application/x-www-form-urlencoded` (JMeter does this automatically for parameters).

Then add an **HTTP Header Manager** under this sampler:
Right-click **POST User Login** → **Add** → **Config Element** → **HTTP Header Manager**
Add header: `Content-Type` = `application/x-www-form-urlencoded`

#### 5e. Data API — GET /api/download/256
Right-click **RC - Data API** → **Add** → **Sampler** → **HTTP Request**
| Field | Value |
|---|---|
| Name | `GET Data API Download` |
| Method | `GET` |
| Path | `/api/download/256` |

---

### Step 6: Add Duration Assertion to ALL HTTP Requests

> **Strategy:** The assignment says set the threshold so that BOTH passes AND failures are observed. Based on the endpoint specs:
> - Home Page baseline: 0–30 ms → set assertion to **25 ms** (will cause some failures)
> - Notice Board: 100–400 ms → set to **150 ms**
> - Course Catalogue: 150–250 ms → set to **200 ms**
> - Login: ~200 ms → set to **150 ms**
> - Download: variable → set to **500 ms**
>
> **Simplest approach for the assignment:** Apply ONE Duration Assertion at the Thread Group level set to **200 ms**, which will cause failures on slower endpoints and passes on fast ones. This clearly shows both outcomes.

Right-click **Low Load - 50 Threads** → **Add** → **Assertions** → **Duration Assertion**
| Field | Value |
|---|---|
| Name | `Duration Assertion - 200ms` |
| Duration in Milliseconds | `200` |

---

### Step 7: Add Listeners to the Thread Group
Right-click **Low Load - 50 Threads** → **Add** → **Listener**

Add these three listeners:
1. **View Results Tree** — Name: `View Results Tree`
2. **Aggregate Report** — Name: `Aggregate Report`
3. **Summary Report** — Name: `Summary Report`

For **Aggregate Report**, set the output filename (to save CSV):
- In the "Filename" field, click Browse and save as:  
  `C:\Users\Sifat\Downloads\yourID_result_50.csv`

---

### Step 8: Create the High Load Thread Group (100 threads)
Right-click **Test Plan** → **Add** → **Threads (Users)** → **Thread Group**

Configure:
| Field | Value |
|---|---|
| Name | `High Load - 100 Threads` |
| Number of Threads | `100` |
| Ramp-Up Period (seconds) | `100` |
| Loop Count | `1` |

**Duplicate all 5 Recording Controllers** from Low Load:
- Right-click **RC - Home Page** → **Copy**
- Right-click **High Load - 100 Threads** → **Paste**
- Repeat for all 5 RCs.

Also add the same **Duration Assertion** and all **3 Listeners** to this Thread Group.

For Aggregate Report filename: `C:\Users\Sifat\Downloads\yourID_result_100.csv`

---

### Step 9: Add the HTTP(S) Test Script Recorder (for compliance)
The assignment requires this element under Non-Test Elements even if you added requests manually.

Right-click **Test Plan** → **Add** → **Non-Test Elements** → **HTTP(S) Test Script Recorder**

Configure:
| Field | Value |
|---|---|
| Port | `8888` |
| Target Controller | `Low Load - 50 Threads > RC - Home Page` |
| Grouping | `Put each group in a new controller` |

> You don't need to actively use this recorder since you've added requests manually. Its presence satisfies the assignment requirement for "incorporating an HTTP(S) Test Script Recorder under Non-Test Elements."

---

### Step 10: Save the Test Plan
**File** → **Save** → Save as: `yourID_testPlan.jmx`
(Replace `yourID` with your actual student ID)

---

## PHASE 3 — Run the Tests

### ⚠️ IMPORTANT: Disable one Thread Group at a time

Before running, **disable the High Load group** first to run Low Load alone:
- Right-click **High Load - 100 Threads** → **Disable**

### Run Low Load (50 threads):
1. Click the **green Run button** (▶) or press **Ctrl+R**
2. Watch **View Results Tree** — you'll see green (pass) and red (fail) entries
3. Let it complete fully
4. **Aggregate Report** → right-click → **Save Table Data** → save as `yourID_result_50.csv`

### Record data from Aggregate Report (50 threads):
Screenshot or manually note down all values for your table:
- Samples, Min, Max, Average, Throughput, Error %
- For each of the 5 endpoints

### Run High Load (100 threads):
1. **Clear All** results: **Run** → **Clear All** (or Ctrl+E)
2. Right-click **Low Load - 50 Threads** → **Disable**
3. Right-click **High Load - 100 Threads** → **Enable**
4. Click **Run** (▶)
5. Save Aggregate Report as `yourID_result_100.csv`

---

## PHASE 4 — Generate HTML Dashboard Report

After running the tests, generate the HTML dashboard via command line.

Open PowerShell and run:

```powershell
cd "C:\Users\Sifat\Downloads\apache-jmeter-5.6.3\apache-jmeter-5.6.3\bin"

# Generate HTML report from your CSV (use the 100-thread CSV for main report)
.\jmeter.bat -g "C:\Users\Sifat\Downloads\yourID_result_100.csv" -o "C:\Users\Sifat\Downloads\yourID_html_report"
```

This creates the HTML dashboard in the output folder.

Then compress it:
- Right-click the `yourID_html_report` folder → **Send to** → **Compressed (zipped) folder**
- Rename to `yourID_html.zip`

---

## PHASE 5 — Fill in the Report Table

Combine your 50-thread and 100-thread Aggregate Report data:

### Table 1: Execution Time and Throughput Comparison

| Target Web Endpoint | Samples | Min (ms) | Max (ms) | Avg (ms) | Throughput | Error % |
|---|---|---|---|---|---|---|
| Home Page (GET /) | 50 | _data_ | _data_ | _data_ | _data_ req/s | _data_ % |
| Home Page (GET /) | 100 | _data_ | _data_ | _data_ | _data_ req/s | _data_ % |
| Notice Board (GET /notices) | 50 | _data_ | _data_ | _data_ | _data_ req/s | _data_ % |
| Notice Board (GET /notices) | 100 | _data_ | _data_ | _data_ | _data_ req/s | _data_ % |
| Course Catalogue (GET /courses) | 50 | _data_ | _data_ | _data_ | _data_ req/s | _data_ % |
| Course Catalogue (GET /courses) | 100 | _data_ | _data_ | _data_ | _data_ req/s | _data_ % |
| User Login (POST /login) | 50 | _data_ | _data_ | _data_ | _data_ req/s | _data_ % |
| User Login (POST /login) | 100 | _data_ | _data_ | _data_ | _data_ req/s | _data_ % |
| Data API (GET /api/download/256) | 50 | _data_ | _data_ | _data_ | _data_ req/s | _data_ % |
| Data API (GET /api/download/256) | 100 | _data_ | _data_ | _data_ | _data_ req/s | _data_ % |

---

## PHASE 6 — Write the Report (Analysis Sections)

### Section a) Execution Time Analysis
Compare Min/Max/Avg across 50 vs 100 threads for all endpoints. Key points to mention:
- Higher thread count typically increases average and max response times
- GET / (Home Page) should remain fast; GET /courses is CPU-intensive and degrades most under load

### Section b) Throughput & Bandwidth Analysis
From the Aggregate Report:
- **Throughput**: requests/sec (higher = better)
- **Received KB/sec**: Data Reception Rate (shown in Aggregate Report)
- **Sent KB/sec**: Data Transmission Rate
- The `/api/download/256` endpoint should show the highest KB/sec values

### Section c) Assertion & Error Rate Analysis
- The 200ms Duration Assertion triggers failures for slower endpoints (Notice Board at 100–400ms, Course Catalogue at 150–250ms)
- Fast endpoints like GET / (0–30ms) will mostly pass
- This creates a realistic mix of pass/fail — exactly what the assignment asks for
- Under high load (100 threads), more assertions fail due to server queuing

---

## PHASE 7 — Video Recording Checklist

Use **OBS Studio** or **Zoom** to record with webcam overlay.

Your video must show:
- [ ] Your face visible in picture-in-picture throughout
- [ ] JMeter GUI with Test Plan structure
- [ ] HTTP(S) Script Recorder configuration
- [ ] Thread Group settings (50 and 100 threads)
- [ ] Duration Assertion setup (show the 200ms threshold)
- [ ] Live test execution — View Results Tree showing green & red results
- [ ] Aggregate Report with populated data
- [ ] HTML Report generation command in PowerShell
- [ ] Opening the generated HTML dashboard in a browser

Upload to YouTube as **Unlisted**, paste the link at the top of your report.

---

## PHASE 8 — Final Submission Checklist

Zip everything together as `yourID_Assessment.zip` containing:

- [ ] `yourID_Assessment.docx` (or .pdf) — Report with YouTube link at top
- [ ] `yourID_testPlan.jmx` — Exported JMeter Test Plan
- [ ] `yourID_result.csv` — Raw CSV from Aggregate Report
- [ ] `yourID_html.zip` — HTML Dashboard archive

---

## 🚨 Common Mistakes to Avoid

1. **Don't run both Thread Groups simultaneously** — disable one before running the other
2. **Clear results between runs** — Run → Clear All before switching Thread Groups
3. **Don't exceed 100 threads** — the server is shared with the whole class
4. **Save the JMX file before recording video** — show the saved file name
5. **CSV must come from JMeter** — don't manually edit it
6. **Duration Assertion must be at right level** — attach to Thread Group or each sampler

---

## 📁 Recommended File Naming

Replace `yourID` with your actual student/registration ID throughout:
- `220041234_testPlan.jmx`
- `220041234_result.csv`
- `220041234_html.zip`
- `220041234_Assessment.pdf`
