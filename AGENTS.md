# GoogleSheet automation


## before github link

```kotlin


/**
* Ads Script Auto-Updater for Prisha Messages
* Clean Professional Design - Only Outer Borders
  */

function onOpen() {
SpreadsheetApp.getUi()
.createMenu('🚀 Ads Updater')
.addItem('📋 Manage Ads Versions', 'showUpdateDialog')
.addToUi();
}

function showUpdateDialog() {
const html = HtmlService.createHtmlOutput(`
    <!DOCTYPE html>
    <html>
      <head>
        <base target="_top">
        <style>
          body { font-family: 'Google Sans', Arial, sans-serif; padding: 20px; background: #f8f9fa; }
          .container {
            max-width: 440px; background: white; border-radius: 16px;
            box-shadow: 0 10px 35px rgba(0,0,0,0.1); padding: 32px; text-align: center;
          }
          h2 { color: #1a73e8; margin: 0 0 10px 0; font-size: 24px; }
          .subtitle { color: #5f6368; margin-bottom: 28px; }
          button {
            width: 100%; padding: 15px; margin: 12px 0; border: none;
            border-radius: 12px; font-size: 16px; font-weight: 500; cursor: pointer;
          }
          .btn-new { background: #1a73e8; color: white; }
          .btn-update { background: #34a853; color: white; }
          button:hover { transform: translateY(-2px); }
        </style>
      </head>
      <body>
        <div class="container">
          <h2>🚀 Ads Script Manager</h2>
          <p class="subtitle">Prisha Messages App</p>
          <button class="btn-new" onclick="google.script.run.withSuccessHandler(success).updateFromJson(false)">
            ➕ Add New Version (At Top)
          </button>
          <button class="btn-update" onclick="google.script.run.withSuccessHandler(success).updateFromJson(true)">
            🔄 Update Existing Version
          </button>
        </div>
        <script>
          function success() {
            alert("✅ Operation Completed Successfully!");
            google.script.host.close();
          }
        </script>
      </body>
    </html>
  `).setWidth(460).setHeight(340);

SpreadsheetApp.getUi().showModalDialog(html, 'Ads Script Manager');
}

// ====================== MAIN FUNCTION ======================
function updateFromJson(isUpdateExisting = false) {
const jsonData =
{
"version": "2.4.0",
"app_id": "ca-app-pub-7441144866662686~7221264456",
"funnel_events": {
"baseline": ["First_splash", "First_home_2.4.0_fdff", "First_setasdefaulted", "First_paywall", "First_language", "First_home_again"],
"variant_a": ["First_splash", "First_paywall", "First_language", "First_home", "First_setasdefaulted"]
},
"paywall_events": [
{"event": "Purchase_setDefault", "description": "app as smsSetDefault dialog click open paywall screen"},
{"event": "Purchased_home", "description": "home screen top icon click open paywall screen"},
{"event": "Purchased_SplashScreen", "description": "in flow 2 after splash open paywall screen"}
],
"ads": [
{"type": "App Open", "name": "Low - App open", "id": "ca-app-pub-2033413118114270/6786602609", "variant": "Default", "notes": "Shows when come from background (10s+), Not in 1st session, 5 min interval"},
{"type": "Large Anchored Banner", "name": "Low - Banner_home 2", "id": "ca-app-pub-2033413118114270/9686409630", "variant": "Default", "notes": "Shows at bottom of Home Screen, after 20s or scroll, 45s interval"},
{"type": "Small Native", "name": "Low - Native_language 1", "id": "ca-app-pub-2033413118114270/3312572970", "variant": "Default", "notes": "Shows at bottom of Language Screen"},
{"type": "Small Native", "name": "Low - MS_Native_Aftercall_Low", "id": "ca-app-pub-2033413118114270/8386429193", "variant": "Default", "notes": "Shows at bottom of Setting Screen"},
{"type": "Small Native", "name": "Low - Native_ViewDetails", "id": "ca-app-pub-2033413118114270/1755846089", "variant": "Default", "notes": "Shows at bottom of View Details Screen"},
{"type": "Interstitial", "name": "Low - inter_chat_back", "id": "ca-app-pub-2033413118114270/2952489636", "variant": "Default", "notes": "Shows at exit of Archived & Recycle Bin Screen"},
{"type": "Rewarded", "name": "Low - New rewarded common", "id": "ca-app-pub-2033413118114270/5289936802", "variant": "Default", "notes": "Backup, Restore, Schedule Message, Translation"}
],
"paywall": {
"default": {
"lifetime": "subscribe_lifetime_message",
"weekly": "subscribe_weekly_message",
"monthly": "subscribe_monthly_messages",
"yearly": "subscribe_yearly_messagess"
},
"experiment": {
"lifetime": "experiment_lifetime_message",
"weekly": "experiment_weekly_messages"
},
"revenue_cat_id": "goog_vwffVeAqEsqRtnAPZwaXudfQnHV",
"experiment_status": "Off"
}
};
const version = jsonData.version;
const sheetName = `V${version}`;

console.log(`🔄 Processing version: ${sheetName} | Mode: ${isUpdateExisting ? 'Update Existing' : 'New Version'}`);

const result = getOrCreateVersionSheet(sheetName, isUpdateExisting);

console.log(`✅ Sheet: ${sheetName} | Status: ${result.isNew ? 'NEW (Inserted at top)' : 'EXISTING (Updated in place)'} | Index: ${result.sheet.getIndex()}`);

buildSheetContent(result.sheet, jsonData);


}



function getOrCreateVersionSheet(sheetName, preferUpdateMode = false) {
const ss = SpreadsheetApp.getActiveSpreadsheet();

// Check if sheet already exists
let sheet = ss.getSheetByName(sheetName);

if (sheet) {
// === EXISTING VERSION ===
console.log(`📋 Found existing sheet: ${sheetName} at index ${sheet.getIndex()}`);
sheet.clear(); // Clear content but keep position
return {
sheet: sheet,
isNew: false
};
}

// === NEW VERSION ===
console.log(`🆕 Creating new version sheet: ${sheetName}`);

// Insert at position 0 (top)
sheet = ss.insertSheet(sheetName, 0);

return {
sheet: sheet,
isNew: true
};
}



// ====================== CLEAN DESIGN WITH OUTER BORDERS ONLY ======================
function buildSheetContent(sheet, data) {
sheet.clear();
let row = 2;

// Main Header
sheet.getRange(1, 1, 1, 7)
.setValue(`🚀 ADS SCRIPT - VERSION ${data.version}`)
.setFontWeight('bold').setFontSize(16).setFontColor('white')
.setBackground('#1a73e8').setHorizontalAlignment('center').merge();

// App ID
sheet.getRange(row++, 2, 1, 2)
.setValues([['App ID:', data.app_id]])
.setFontWeight('bold').setBackground('#e8f0fe');

row += 1;

// === FUNNEL EVENTS ===
sheet.getRange(row++, 2, 1, 6).setValue('📊 FUNNEL EVENTS')
.setFontWeight('bold').setFontSize(13).setBackground('#34a853').setFontColor('white').merge();

const fHeader = sheet.getRange(row++, 2, 1, 3);
fHeader.setValues([['No.', 'Variant Baseline', 'Variant A']])
.setBackground('#4285f4').setFontColor('white').setFontWeight('bold');

const baseline = data.funnel_events.baseline || [];
const varia = data.funnel_events.variant_a || [];
for (let i = 0; i < Math.max(baseline.length, varia.length); i++) {
sheet.getRange(row++, 2, 1, 3).setValues([[i+1, baseline[i] || '', varia[i] || '']]);
}
// Outer border only
sheet.getRange(row - Math.max(baseline.length, varia.length) - 1, 2, Math.max(baseline.length, varia.length) + 2, 3)
.setBorder(true, true, true, true, false, false);

// === PAYWALL EVENTS ===
row += 2;
sheet.getRange(row++, 2, 1, 6).setValue('💰 PAYWALL EVENTS')
.setFontWeight('bold').setFontSize(13).setBackground('#34a853').setFontColor('white').merge();

const pHeader = sheet.getRange(row++, 2, 1, 3);
pHeader.setValues([['No.', 'Event Name', 'Description']])
.setBackground('#4285f4').setFontColor('white').setFontWeight('bold');

data.paywall_events.forEach((item, i) => {
sheet.getRange(row++, 2, 1, 3).setValues([[i+1, item.event, item.description]]);
});
sheet.getRange(row - data.paywall_events.length - 2, 2, data.paywall_events.length + 2, 3)
.setBorder(true, true, true, true, false, false);

// === ADS CONFIGURATION ===
row += 2;
sheet.getRange(row++, 2, 1, 6).setValue('📢 ADS CONFIGURATION')
.setFontWeight('bold').setFontSize(13).setBackground('#34a853').setFontColor('white').merge();

const aHeader = sheet.getRange(row++, 2, 1, 6);
aHeader.setValues([['No.', 'Ad Type', 'Ad Name', 'Ad Unit ID', 'Variant', 'Notes']])
.setBackground('#4285f4').setFontColor('white').setFontWeight('bold');

data.ads.forEach((ad, i) => {
sheet.getRange(row++, 2, 1, 6).setValues([[i+1, ad.type, ad.name, ad.id, ad.variant, ad.notes]]);
});
sheet.getRange(row - data.ads.length - 2, 2, data.ads.length + 2, 6)
.setBorder(true, true, true, true, false, false);

// === PAYWALL PLANS ===
row += 2;
sheet.getRange(row++, 2, 1, 6).setValue('💎 PAYWALL PLANS')
.setFontWeight('bold').setFontSize(13).setBackground('#34a853').setFontColor('white').merge();

const plHeader = sheet.getRange(row++, 2, 1, 3);
plHeader.setValues([['Type', 'Plan Name', 'RevenueCat ID']])
.setBackground('#4285f4').setFontColor('white').setFontWeight('bold');

Object.keys(data.paywall.default).forEach(key => {
sheet.getRange(row++, 2, 1, 3).setValues([['Default', key.charAt(0).toUpperCase() + key.slice(1), data.paywall.default[key]]]);
});
Object.keys(data.paywall.experiment).forEach(key => {
sheet.getRange(row++, 2, 1, 3).setValues([['Experiment', key.charAt(0).toUpperCase() + key.slice(1), data.paywall.experiment[key]]]);
});
sheet.getRange(row - Object.keys(data.paywall.default).length - Object.keys(data.paywall.experiment).length - 2, 2,
Object.keys(data.paywall.default).length + Object.keys(data.paywall.experiment).length + 2, 3)
.setBorder(true, true, true, true, false, false);

// RevenueCat Info
row += 1;
sheet.getRange(row++, 2, 1, 2).setValues([['RevenueCat ID:', data.paywall.revenue_cat_id]]).setFontWeight('bold');
sheet.getRange(row++, 2, 1, 2).setValues([['Experiment Status:', data.paywall.experiment_status]]).setFontWeight('bold');

sheet.autoResizeColumns(1, 7);
sheet.getRange(1, 6, sheet.getLastRow(), 1).setWrap(true);
}

```

# with git link

## this is command to push github only change according version
```kotlin
cd /Volumes/Extra/Fenisha/MessagerApp/2.4.1/MaterialMessages
git add app/src/main/assets/ads_config.json
git commit -m "Update ads config to version 2.4.6”
git push origin main
```

```kotlin


/**
* Ads Script Auto-Updater for Prisha Messages
* Clean Professional Design - Only Outer Borders
  */

function onOpen() {
SpreadsheetApp.getUi()
.createMenu('🚀 Ads Updater')
.addItem('📋 Manage Ads Versions', 'showUpdateDialog')
.addToUi();
}

function showUpdateDialog() {
const html = HtmlService.createHtmlOutput(`
    <!DOCTYPE html>
    <html>
      <head>
        <base target="_top">
        <style>
          body { font-family: 'Google Sans', Arial, sans-serif; padding: 20px; background: #f8f9fa; }
          .container {
            max-width: 440px; background: white; border-radius: 16px;
            box-shadow: 0 10px 35px rgba(0,0,0,0.1); padding: 32px; text-align: center;
          }
          h2 { color: #1a73e8; margin: 0 0 10px 0; font-size: 24px; }
          .subtitle { color: #5f6368; margin-bottom: 28px; }
          button {
            width: 100%; padding: 15px; margin: 12px 0; border: none;
            border-radius: 12px; font-size: 16px; font-weight: 500; cursor: pointer;
          }
          .btn-new { background: #1a73e8; color: white; }
          .btn-update { background: #34a853; color: white; }
          button:hover { transform: translateY(-2px); }
        </style>
      </head>
      <body>
        <div class="container">
          <h2>🚀 Ads Script Manager</h2>
          <p class="subtitle">Prisha Messages App</p>
          <button class="btn-new" onclick="google.script.run.withSuccessHandler(success).updateFromJson(false)">
            ➕ Add New Version (At Top)
          </button>
          <button class="btn-update" onclick="google.script.run.withSuccessHandler(success).updateFromJson(true)">
            🔄 Update Existing Version
          </button>
        </div>
        <script>
          function success() {
            alert("✅ Operation Completed Successfully!");
            google.script.host.close();
          }
        </script>
      </body>
    </html>
  `).setWidth(460).setHeight(340);

SpreadsheetApp.getUi().showModalDialog(html, 'Ads Script Manager');
}

// ====================== MAIN FUNCTION ======================
function updateFromJson(isUpdateExisting = false) {
const JSON_URL = "https://raw.githubusercontent.com/fenisha-vasundhara/adsScript/main/app/src/main/assets/ads_config.json";

try {
console.log("📥 Fetching latest config from GitHub...");

    const response = UrlFetchApp.fetch(JSON_URL, { 
      muteHttpExceptions: true,
      headers: { 'Cache-Control': 'no-cache' }
    });
    
    if (response.getResponseCode() !== 200) {
      throw new Error(`Failed to fetch: ${response.getResponseCode()}`);
    }

    const jsonData = JSON.parse(response.getContentText());
    const version = jsonData.version || "Unknown";
    const sheetName = `V${version}`;

    console.log(`🔄 Processing version: ${sheetName}`);

    const result = getOrCreateVersionSheet(sheetName, isUpdateExisting);
    buildSheetContent(result.sheet, jsonData);

    SpreadsheetApp.getUi().alert("✅ Success!", 
      `Version ${version} loaded successfully from GitHub.`, 
      SpreadsheetApp.getUi().ButtonSet.OK);

} catch (error) {
console.error("❌ Error:", error);
SpreadsheetApp.getUi().alert("❌ Error",
"Failed to load JSON from GitHub.\n\nMake sure ads_config.json exists in the repo.\n\nError: " + error.toString(),
SpreadsheetApp.getUi().ButtonSet.OK);
}
}
function getOrCreateVersionSheet(sheetName, preferUpdateMode = false) {
const ss = SpreadsheetApp.getActiveSpreadsheet();

// Check if sheet already exists
let sheet = ss.getSheetByName(sheetName);

if (sheet) {
// === EXISTING VERSION ===
console.log(`📋 Found existing sheet: ${sheetName} at index ${sheet.getIndex()}`);
sheet.clear(); // Clear content but keep position
return {
sheet: sheet,
isNew: false
};
}

// === NEW VERSION ===
console.log(`🆕 Creating new version sheet: ${sheetName}`);

// Insert at position 0 (top)
sheet = ss.insertSheet(sheetName, 0);

return {
sheet: sheet,
isNew: true
};
}



// ====================== CLEAN DESIGN WITH OUTER BORDERS ONLY ======================
function buildSheetContent(sheet, data) {
sheet.clear();
let row = 2;

// Main Header
sheet.getRange(1, 1, 1, 7)
.setValue(`🚀 ADS SCRIPT - VERSION ${data.version}`)
.setFontWeight('bold').setFontSize(16).setFontColor('white')
.setBackground('#1a73e8').setHorizontalAlignment('center').merge();

// App ID
sheet.getRange(row++, 2, 1, 2)
.setValues([['App ID:', data.app_id]])
.setFontWeight('bold').setBackground('#e8f0fe');

row += 1;

// === FUNNEL EVENTS ===
sheet.getRange(row++, 2, 1, 6).setValue('📊 FUNNEL EVENTS')
.setFontWeight('bold').setFontSize(13).setBackground('#34a853').setFontColor('white').merge();

const fHeader = sheet.getRange(row++, 2, 1, 3);
fHeader.setValues([['No.', 'Variant Baseline', 'Variant A']])
.setBackground('#4285f4').setFontColor('white').setFontWeight('bold');

const baseline = data.funnel_events.baseline || [];
const varia = data.funnel_events.variant_a || [];
for (let i = 0; i < Math.max(baseline.length, varia.length); i++) {
sheet.getRange(row++, 2, 1, 3).setValues([[i+1, baseline[i] || '', varia[i] || '']]);
}
// Outer border only
sheet.getRange(row - Math.max(baseline.length, varia.length) - 1, 2, Math.max(baseline.length, varia.length) + 2, 3)
.setBorder(true, true, true, true, false, false);

// === PAYWALL EVENTS ===
row += 2;
sheet.getRange(row++, 2, 1, 6).setValue('💰 PAYWALL EVENTS')
.setFontWeight('bold').setFontSize(13).setBackground('#34a853').setFontColor('white').merge();

const pHeader = sheet.getRange(row++, 2, 1, 3);
pHeader.setValues([['No.', 'Event Name', 'Description']])
.setBackground('#4285f4').setFontColor('white').setFontWeight('bold');

data.paywall_events.forEach((item, i) => {
sheet.getRange(row++, 2, 1, 3).setValues([[i+1, item.event, item.description]]);
});
sheet.getRange(row - data.paywall_events.length - 2, 2, data.paywall_events.length + 2, 3)
.setBorder(true, true, true, true, false, false);

// === ADS CONFIGURATION ===
row += 2;
sheet.getRange(row++, 2, 1, 6).setValue('📢 ADS CONFIGURATION')
.setFontWeight('bold').setFontSize(13).setBackground('#34a853').setFontColor('white').merge();

const aHeader = sheet.getRange(row++, 2, 1, 6);
aHeader.setValues([['No.', 'Ad Type', 'Ad Name', 'Ad Unit ID', 'Variant', 'Notes']])
.setBackground('#4285f4').setFontColor('white').setFontWeight('bold');

data.ads.forEach((ad, i) => {
sheet.getRange(row++, 2, 1, 6).setValues([[i+1, ad.type, ad.name, ad.id, ad.variant, ad.notes]]);
});
sheet.getRange(row - data.ads.length - 2, 2, data.ads.length + 2, 6)
.setBorder(true, true, true, true, false, false);

// === PAYWALL PLANS ===
row += 2;
sheet.getRange(row++, 2, 1, 6).setValue('💎 PAYWALL PLANS')
.setFontWeight('bold').setFontSize(13).setBackground('#34a853').setFontColor('white').merge();

const plHeader = sheet.getRange(row++, 2, 1, 3);
plHeader.setValues([['Type', 'Plan Name', 'RevenueCat ID']])
.setBackground('#4285f4').setFontColor('white').setFontWeight('bold');

Object.keys(data.paywall.default).forEach(key => {
sheet.getRange(row++, 2, 1, 3).setValues([['Default', key.charAt(0).toUpperCase() + key.slice(1), data.paywall.default[key]]]);
});
Object.keys(data.paywall.experiment).forEach(key => {
sheet.getRange(row++, 2, 1, 3).setValues([['Experiment', key.charAt(0).toUpperCase() + key.slice(1), data.paywall.experiment[key]]]);
});
sheet.getRange(row - Object.keys(data.paywall.default).length - Object.keys(data.paywall.experiment).length - 2, 2,
Object.keys(data.paywall.default).length + Object.keys(data.paywall.experiment).length + 2, 3)
.setBorder(true, true, true, true, false, false);

// RevenueCat Info
row += 1;
sheet.getRange(row++, 2, 1, 2).setValues([['RevenueCat ID:', data.paywall.revenue_cat_id]]).setFontWeight('bold');
sheet.getRange(row++, 2, 1, 2).setValues([['Experiment Status:', data.paywall.experiment_status]]).setFontWeight('bold');

sheet.autoResizeColumns(1, 7);
sheet.getRange(1, 6, sheet.getLastRow(), 1).setWrap(true);
}
```

# 10-8-2026



```kotlin
cd /Volumes/Extra/Fenisha/MessagerApp/2.4.1/MaterialMessages
git add app/src/main/assets/ads_config.json
git commit -m "Update ads config to version 2.4.7”
git push origin main
```

```kotlin
/**
 * Ads Script Auto-Updater for Prisha Messages
 * Clean Professional Design - Only Outer Borders
 */

function onOpen() {
  SpreadsheetApp.getUi()
    .createMenu('🚀 Ads Updater')
    .addItem('🔄 Update Ads from GitHub', 'updateFromJson')
    .addToUi();
}

// ====================== MAIN FUNCTION ======================
function updateFromJson() {
  const baseUrl = "https://raw.githubusercontent.com/fenisha-vasundhara/adsScript/main/app/src/main/assets/ads_config.json";
  const JSON_URL = baseUrl + "?t=" + new Date().getTime();   // Strong cache buster
  
  try {
    console.log("📥 Fetching latest config from GitHub...");

    const response = UrlFetchApp.fetch(JSON_URL, { 
      muteHttpExceptions: true,
      headers: { 
        'Cache-Control': 'no-cache, no-store, must-revalidate',
        'Pragma': 'no-cache',
        'Expires': '0'
      }
    });
    
    if (response.getResponseCode() !== 200) {
      throw new Error(`HTTP Error: ${response.getResponseCode()}`);
    }

    const jsonText = response.getContentText();
    const jsonData = JSON.parse(jsonText);
    
    console.log(`✅ Successfully fetched Version: ${jsonData.version}`);

    const sheetName = `V${jsonData.version}`;
    const result = getOrCreateVersionSheet(sheetName);
    
    buildSheetContent(result.sheet, jsonData);

    SpreadsheetApp.getUi().alert("✅ Success!", 
      `Version ${jsonData.version} has been ${result.isNew ? 'created' : 'updated'} successfully.\n\nLast Updated: ${new Date().toLocaleString()}`, 
      SpreadsheetApp.getUi().ButtonSet.OK);
    
  } catch (error) {
    console.error("❌ Error:", error);
    SpreadsheetApp.getUi().alert("❌ Update Failed", 
      "Could not load JSON from GitHub.\n\nError: " + error.toString(), 
      SpreadsheetApp.getUi().ButtonSet.OK);
  }
}
function getOrCreateVersionSheet(sheetName) {
  const ss = SpreadsheetApp.getActiveSpreadsheet();
  let sheet = ss.getSheetByName(sheetName);
  
  if (sheet) {
    console.log(`📋 Updating existing sheet: ${sheetName}`);
    sheet.clear();
    return { sheet: sheet, isNew: false };
  } 
  
  console.log(`🆕 Creating new sheet: ${sheetName}`);
  sheet = ss.insertSheet(sheetName, 0);
  
  return { sheet: sheet, isNew: true };
}

// ====================== BUILD SHEET ======================
function buildSheetContent(sheet, data) {
  sheet.clear();
  let row = 2;

  // Main Header
  sheet.getRange(1, 1, 1, 7)
    .setValue(`🚀 ADS SCRIPT - VERSION ${data.version}`)
    .setFontWeight('bold').setFontSize(16).setFontColor('white')
    .setBackground('#1a73e8').setHorizontalAlignment('center').merge();

  // App ID
  sheet.getRange(row++, 2, 1, 2)
    .setValues([['App ID:', data.app_id]])
    .setFontWeight('bold').setBackground('#e8f0fe');

  row += 1;

  // FUNNEL EVENTS
  sheet.getRange(row++, 2, 1, 6).setValue('📊 FUNNEL EVENTS')
    .setFontWeight('bold').setFontSize(13).setBackground('#34a853').setFontColor('white').merge();

  const fHeader = sheet.getRange(row++, 2, 1, 3);
  fHeader.setValues([['No.', 'Variant Baseline', 'Variant A']])
    .setBackground('#4285f4').setFontColor('white').setFontWeight('bold');

  const baseline = data.funnel_events?.baseline || [];
  const varia = data.funnel_events?.variant_a || [];
  for (let i = 0; i < Math.max(baseline.length, varia.length); i++) {
    sheet.getRange(row++, 2, 1, 3).setValues([[i+1, baseline[i] || '', varia[i] || '']]);
  }
  sheet.getRange(row - Math.max(baseline.length, varia.length) - 1, 2, Math.max(baseline.length, varia.length) + 2, 3)
    .setBorder(true, true, true, true, false, false);

  // PAYWALL EVENTS
  row += 2;
  sheet.getRange(row++, 2, 1, 6).setValue('💰 PAYWALL EVENTS')
    .setFontWeight('bold').setFontSize(13).setBackground('#34a853').setFontColor('white').merge();

  const pHeader = sheet.getRange(row++, 2, 1, 3);
  pHeader.setValues([['No.', 'Event Name', 'Description']])
    .setBackground('#4285f4').setFontColor('white').setFontWeight('bold');

  data.paywall_events.forEach((item, i) => {
    sheet.getRange(row++, 2, 1, 3).setValues([[i+1, item.event, item.description]]);
  });
  sheet.getRange(row - data.paywall_events.length - 2, 2, data.paywall_events.length + 2, 3)
    .setBorder(true, true, true, true, false, false);

  // ADS CONFIGURATION
  row += 2;
  sheet.getRange(row++, 2, 1, 6).setValue('📢 ADS CONFIGURATION')
    .setFontWeight('bold').setFontSize(13).setBackground('#34a853').setFontColor('white').merge();

  const aHeader = sheet.getRange(row++, 2, 1, 6);
  aHeader.setValues([['No.', 'Ad Type', 'Ad Name', 'Ad Unit ID', 'Variant', 'Notes']])
    .setBackground('#4285f4').setFontColor('white').setFontWeight('bold');

  data.ads.forEach((ad, i) => {
    sheet.getRange(row++, 2, 1, 6).setValues([[i+1, ad.type, ad.name, ad.id, ad.variant, ad.notes]]);
  });
  sheet.getRange(row - data.ads.length - 2, 2, data.ads.length + 2, 6)
    .setBorder(true, true, true, true, false, false);

  // PAYWALL PLANS
  row += 2;
  sheet.getRange(row++, 2, 1, 6).setValue('💎 PAYWALL PLANS')
    .setFontWeight('bold').setFontSize(13).setBackground('#34a853').setFontColor('white').merge();

  const plHeader = sheet.getRange(row++, 2, 1, 3);
  plHeader.setValues([['Type', 'Plan Name', 'RevenueCat ID']])
    .setBackground('#4285f4').setFontColor('white').setFontWeight('bold');

  Object.keys(data.paywall.default).forEach(key => {
    sheet.getRange(row++, 2, 1, 3).setValues([['Default', key.charAt(0).toUpperCase() + key.slice(1), data.paywall.default[key]]]);
  });
  Object.keys(data.paywall.experiment).forEach(key => {
    sheet.getRange(row++, 2, 1, 3).setValues([['Experiment', key.charAt(0).toUpperCase() + key.slice(1), data.paywall.experiment[key]]]);
  });
  sheet.getRange(row - Object.keys(data.paywall.default).length - Object.keys(data.paywall.experiment).length - 2, 2, 
                Object.keys(data.paywall.default).length + Object.keys(data.paywall.experiment).length + 2, 3)
    .setBorder(true, true, true, true, false, false);

  // RevenueCat Info
  row += 1;
  sheet.getRange(row++, 2, 1, 2).setValues([['RevenueCat ID:', data.paywall.revenue_cat_id]]).setFontWeight('bold');
  sheet.getRange(row++, 2, 1, 2).setValues([['Experiment Status:', data.paywall.experiment_status]]).setFontWeight('bold');

  sheet.autoResizeColumns(1, 7);
  sheet.getRange(1, 6, sheet.getLastRow(), 1).setWrap(true);
}
```


# one sheet for all  formate wise
```kotlin


cd "/Volumes/Extra/Fenisha/MessagerApp/2.4.1/MaterialMessages"

git add app/src/main/assets/ads_config.json

git commit -m "Update ads config to version 2.4.8"

git push origin main

curl -X POST "https://script.google.com/macros/s/AKfycbwTRjK3kTzRK13cnH80E2Dm1lnA2n9qijEbVkNrpWJ7Xf6PXR6fcDvNlHz0fkXSBOeJ/exec"
//-----------------------

//VERSION=$2.4.8

VERSION="2.2.2"

cd "/Volumes/Extra/Fenisha/MessagerApp/2.4.1/MaterialMessages" || exit

git add app/src/main/assets/ads_config.json

git commit -m "Update ads config to version $VERSION"

git push origin main

 curl -X POST "https://script.google.com/macros/s/AKfycbwTRjK3kTzRK13cnH80E2Dm1lnA2n9qijEbVkNrpWJ7Xf6PXR6fcDvNlHz0fkXSBOeJ/exec"

echo "Done: Version $VERSION pushed and sheet updated."


//==========================
cd "/Volumes/Extra/Fenisha/MessagerApp/2.4.1/MaterialMessages" || exit

VERSION="2.2.3" && \
FILE="app/src/main/assets/ads_config.json" && \
SCRIPT_URL="https://script.google.com/macros/s/AKfycbwTRjK3kTzRK13cnH80E2Dm1lnA2n9qijEbVkNrpWJ7Xf6PXR6fcDvNlHz0fkXSBOeJ/exec" && \
perl -i -pe "s/\"version\"\s*:\s*\"[^\"]+\"/\"version\": \"$VERSION\"/" "$FILE" && \
git add "$FILE" && \
git commit -m "Update ads config $VERSION" && \
git push origin main && \
curl -X POST "$SCRIPT_URL" -H "Content-Type: application/json" -d "{\"version\":\"$VERSION\"}"



```
```kotlin
/**
 * Ads Script Auto-Updater for Prisha Messages
 * Clean Professional Design - Only Outer Borders
 */
function doPost(e) {
    updateFromJson();

    return ContentService
        .createTextOutput("Success");
}
function onOpen() {
    SpreadsheetApp.getUi()
        .createMenu('🚀 Ads Updater')
        .addItem('🔄 Update Ads from GitHub', 'updateFromJson')
        .addToUi();
}

// ====================== MAIN FUNCTION ======================
function updateFromJson() {
    const baseUrl = "https://raw.githubusercontent.com/fenisha-vasundhara/adsScript/main/app/src/main/assets/ads_config.json";
    const JSON_URL = baseUrl + "?t=" + new Date().getTime();

    try {

        const response = UrlFetchApp.fetch(JSON_URL, {
            muteHttpExceptions: true,
            headers: {
            'Cache-Control': 'no-cache, no-store, must-revalidate'
        }
        });

        if (response.getResponseCode() !== 200) {
            throw new Error(`HTTP Error: ${response.getResponseCode()}`);
        }

        const jsonData = JSON.parse(response.getContentText());

        const sheet = getDocumentationSheet();

        if (versionAlreadyExists(sheet, jsonData.version)) {
            SpreadsheetApp.getUi().alert(
                `Version ${jsonData.version} already exists.`
            );
            return;
        }

        insertVersionBlock(sheet, jsonData);

        SpreadsheetApp.getUi().alert(
            `Version ${jsonData.version} added successfully.`
        );

    } catch (error) {

        SpreadsheetApp.getUi().alert(
            "Update Failed\n\n" + error
        );

    }
}
function getDocumentationSheet() {

    const ss = SpreadsheetApp.getActiveSpreadsheet();

    let sheet = ss.getSheetByName("Ads Documentation");

    if (!sheet) {

        sheet = ss.insertSheet("Ads Documentation", 0);

        sheet.getRange("A1:G1")
            .merge()
            .setValue("🚀 ADS DOCUMENTATION")
            .setBackground("#1a73e8")
            .setFontColor("white")
            .setFontWeight("bold")
            .setFontSize(18)
            .setHorizontalAlignment("center");
    }

    return sheet;
}
function versionAlreadyExists(sheet, version) {

    const values = sheet.getDataRange().getValues();

    for (let i = 0; i < values.length; i++) {

    if (
        String(values[i][0]).trim() ===
        `VERSION ${version}`
    ) {
        return true;
    }
}

    return false;
}
function insertVersionBlock(sheet, data) {

    const startRow = 3;

    const rowsNeeded = calculateRowsNeeded(data);

    sheet.insertRowsBefore(startRow, rowsNeeded);



    buildVersionContent(sheet, startRow, data);
}
function calculateRowsNeeded(data) {

    let rows = 20;

    rows += Math.max(
        data.funnel_events?.baseline?.length || 0,
        data.funnel_events?.variant_a?.length || 0
    );

    rows += data.paywall_events?.length || 0;

    rows += data.ads?.length || 0;

    rows += Object.keys(data.paywall.default || {}).length;

    rows += Object.keys(data.paywall.experiment || {}).length;

    return rows + 20;
}

function getOrCreateVersionSheet(sheetName) {
    const ss = SpreadsheetApp.getActiveSpreadsheet();
    let sheet = ss.getSheetByName(sheetName);

    if (sheet) {
        console.log(`📋 Updating existing sheet: ${sheetName}`);
        sheet.clear();
        return { sheet: sheet, isNew: false };
    }

    console.log(`🆕 Creating new sheet: ${sheetName}`);
    sheet = ss.insertSheet(sheetName, 0);

    return { sheet: sheet, isNew: true };
}

// ====================== BUILD SHEET ======================
function buildVersionContent(sheet, startRow, data) {

    let row = startRow;
// Clear formatting for this new version block
// sheet.getRange(startRow, 1, calculateRowsNeeded(data), 7)
//   .setBackground("white")
//   .setFontColor("black")
//   .setFontWeight("normal");



    sheet.getRange(row,1,1,7)
        .merge()
        .setValue(`VERSION ${data.version}`)
        //   .setBackground("#1a73e8")
        //   .setFontColor("white")
        .setFontWeight("bold")
        .setFontSize(16);

    row++;

    sheet.getRange(row++,1)
        .setValue(
            "Updated : " +
                    Utilities.formatDate(
                        new Date(),
                        Session.getScriptTimeZone(),
                        "dd-MMM-yyyy HH:mm:ss"
                    )
        );

    row++;

    sheet.getRange(row++,2,1,2)
        .setValues([["App ID", data.app_id]])
        .setFontWeight("bold");

    row++;

    // FUNNEL EVENTS

    sheet.getRange(row++,2,1,3)
        .merge()
        .setValue("📊 FUNNEL EVENTS")
        .setBackground("#34a853")
        .setFontColor("white")
        .setFontWeight("bold");

    sheet.getRange(row++,2,1,3)
        .setValues([["No.","Variant Baseline","Variant A"]])
        .setBackground("#4285f4")
        .setFontColor("white");

    const baseline = data.funnel_events?.baseline || [];
    const variantA = data.funnel_events?.variant_a || [];

    for (let i = 0; i < Math.max(baseline.length, variantA.length); i++) {

    sheet.getRange(row++,2,1,3)
        .setValues([
            [i+1, baseline[i] || "", variantA[i] || ""]
        ]);
}

    row += 2;

    // PAYWALL EVENTS

    sheet.getRange(row++,2,1,3)
        .merge()
        .setValue("💰 PAYWALL EVENTS")
        .setBackground("#34a853")
        .setFontColor("white")
        .setFontWeight("bold");

    sheet.getRange(row++,2,1,3)
        .setValues([["No.","Event","Description"]])
        .setBackground("#4285f4")
        .setFontColor("white");

    data.paywall_events.forEach((item,index)=>{

    sheet.getRange(row++,2,1,3)
        .setValues([
            [index+1,item.event,item.description]
        ]);

});

    row += 2;

    // ADS

    sheet.getRange(row++,2,1,6)
        .merge()
        .setValue("📢 ADS CONFIGURATION")
        .setBackground("#34a853")
        .setFontColor("white")
        .setFontWeight("bold");

    sheet.getRange(row++,2,1,6)
        .setValues([
            ["No.","Type","Name","Ad Unit ID","Variant","Notes"]
        ])
        .setBackground("#4285f4")
        .setFontColor("white");

    data.ads.forEach((ad,index)=>{

    sheet.getRange(row++,2,1,6)
        .setValues([
            [
                index+1,
                ad.type,
                ad.name,
                ad.id,
                ad.variant,
                ad.notes
            ]
        ]);

});

    row += 2;

    // PAYWALL

    sheet.getRange(row++,2,1,3)
        .merge()
        .setValue("💎 PAYWALL PLANS")
        .setBackground("#34a853")
        .setFontColor("white")
        .setFontWeight("bold");

    sheet.getRange(row++,2,1,3)
        .setValues([
            ["Type","Plan","RevenueCat ID"]
        ])
        .setBackground("#4285f4")
        .setFontColor("white");

    Object.keys(data.paywall.default).forEach(key => {

        sheet.getRange(row++,2,1,3)
            .setValues([
                [
                    "Default",
                    key,
                    data.paywall.default[key]
                ]
            ]);
    });

    Object.keys(data.paywall.experiment).forEach(key => {

        sheet.getRange(row++,2,1,3)
            .setValues([
                [
                    "Experiment",
                    key,
                    data.paywall.experiment[key]
                ]
            ]);
    });

    row++;

    sheet.getRange(row++,2,1,2)
        .setValues([
            ["RevenueCat ID",data.paywall.revenue_cat_id]
        ]);

    sheet.getRange(row++,2,1,2)
        .setValues([
            ["Experiment Status",data.paywall.experiment_status]
        ]);

    row++;

    sheet.getRange(row,1,1,7)
        .merge()
        .setValue("────────────────────────────────────");


}
```




