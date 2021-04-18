// <=== REPLACE ===>
function initTabs(){
    document.querySelectorAll("div[tabs-section]")
        .forEach(element => {
            showCorrespondingTabBody(element)
            element.addEventListener('click', (event) => toggleSectionsEventHandler(event))
        })
    let cached = localStorage.getItem("active-tab")
    if (cached) {
        let parsed = JSON.parse(cached)
        let tab = document.querySelector('div[tabs-section] > button[data-togglable="' + parsed + '"]')
        if(tab) {
            toggleSections(tab)
        }
    }
}
// <=== WITH ===>
function initTabs() {
    document.querySelectorAll("div[tabs-section]")
        .forEach(element => {
            showCorrespondingTabBody(element)
            element.addEventListener('click', (event) => toggleSectionsEventHandler(event))
        })
    let cached = localStorage.getItem(document.title.startsWith("com.") ? "package-tab" : "class-tab")
    if(!cached) {
        cached = document.title.startsWith("com.") ? '"Types"' : '"Functions"'
    }
    if (cached) {
        let parsed = JSON.parse(cached)
        let tab = document.querySelector('div[tabs-section] > button[data-togglable="' + parsed + '"]')
        if(tab) {
            toggleSections(tab, true)
        }
    }
}
// <=== REPLACE ===>
function toggleSections(target) {
    localStorage.setItem('active-tab', JSON.stringify(target.getAttribute("data-togglable")))
    const activateTabs = (containerClass) => {
        for(const element of document.getElementsByClassName(containerClass)){
            for(const child of element.children){
                if(child.getAttribute("data-togglable") === target.getAttribute("data-togglable")){
                    child.setAttribute("data-active", "")
                } else {
                    child.removeAttribute("data-active")
                }
            }
        }
    }

    activateTabs("tabs-section")
    activateTabs("tabs-section-body")
}
// <=== WITH ===>
function toggleSections(target, skipStorage) {
    if(!skipStorage)
        localStorage.setItem('active-tab', JSON.stringify(target.getAttribute("data-togglable")))
    const activateTabs = (containerClass) => {
        for(const element of document.getElementsByClassName(containerClass)){
            for(const child of element.children){
                if(child.getAttribute("data-togglable") === target.getAttribute("data-togglable")){
                    child.setAttribute("data-active", "")
                } else {
                    child.removeAttribute("data-active")
                }
            }
        }
    }

    activateTabs("tabs-section")
    activateTabs("tabs-section-body")
}