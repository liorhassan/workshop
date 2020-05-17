document.addEventListener("DOMContentLoaded", function () {
    const stores = [
        {
            "name": "Castro",
            "type": "Owner",
            "options": ["Add Manager", "Add Owner", "Remove Manager", "Edit Permissions", "Manage Supply", "View Purchasing History"]
        },
        {
            "name": "Mango",
            "type": "Manager",
            "options": ["Manage Supply", "View Purchasing History"]
        },
        {
            "name": "Delta",
            "type": "Owner",
            "options": ["Add Manager", "Add Owner", "Remove Manager", "Edit Permissions", "Manage Supply", "View Purchasing History"]
        },
        {
            "name": "Zara",
            "type": "Manager",
            "options": ["View Purchasing History"]
        }
    ]

    // fetch("localhost:8000/myStores")
    //     .then(response => response.json())
    //     .then(setMyStores)
    setMyStores(stores);

    // When the user clicks on <span> (x), close the modal
    var x_buttons = document.getElementsByClassName("close");
    for (let x_button of x_buttons) {
        x_button.addEventListener("click", function () {
            var modals = document.getElementsByClassName("modal");
            for (let modal of modals) {
                model.style.display = "none";
            }
        });
    }
});

function setMyStores(stores) {
    const numOfStores = document.getElementById("num-of-stores");
    numOfStores.innerHTML = "";
    numOfStores.append(document.createTextNode(stores.length));

    const stores_list = document.getElementById("stores-list");

    stores_list.innerHTML = "";

    stores.forEach(currStore => {
        const element = document.createElement("li");
        element.classList.add("list-group-item");
        element.classList.add("d-flex");
        element.classList.add("justify-content-between");
        element.classList.add("lh-condensed");
        
        const element_div = document.createElement("div");
        
        const store_name = document.createElement("h6");
        store_name.classList.add("my-0");
        store_name.append(document.createTextNode(currStore.name));

        const title = document.createElement("small");
        title.classList.add("text-muted");
        title.append(document.createTextNode(currStore.type));

        element_div.appendChild(store_name);
        element_div.appendChild(title);

        const actions = document.createElement("span");

        const action_div = document.createElement("div");
        action_div.classList.add("input-group-append");

        const action_button = document.createElement("button");
        action_button.append(document.createTextNode("Actions"));
        action_button.classList.add("btn");
        action_button.classList.add("btn-outline-secondary");
        action_button.classList.add("dropdown-toggle");
        action_button.type = "button";
        action_button.setAttribute("data-toggle", "dropdown");
        action_button.setAttribute("aria-haspopup", "true");
        action_button.setAttribute("aria-expanded", "false");

        const action_dropdown = document.createElement("div");
        action_dropdown.id = "menu_dropDown";
        action_dropdown.classList.add("dropdown-menu");

        currStore.options.forEach(currAction=>{
            const a = document.createElement("a");
            a.addEventListener("click",function(){
                showPopUp(currAction);
            })
            a.classList.add("dropdown-item");
            a.append(document.createTextNode(currAction));

            action_dropdown.appendChild(a);  
        })

        action_div.appendChild(action_button);
        action_div.appendChild(action_dropdown);
        actions.appendChild(action_div);
        
        element.appendChild(element_div);
        element.appendChild(actions);
        
        stores_list.appendChild(element);
    })
}
    

actionToModel={
    "Add Manager":"addManagerModal",
    "Add Owner":"addOwnerModal",
    "Remove Manager":"removeManagerModal",
    "Edit Permissions":"editPermissionsModal",
    "Manage Supply":"manageSupplyModal",
    "View Purchasing History":"viewPurchaseHistoryModal"
}

function showPopUp(action){
    
    document.getElementById(actionToModel[action]).style.display = "block";
}

// When the user clicks anywhere outside of the modal, close it
window.onclick = function (event) {
    if (event.target == document.getElementById("editModal")) {
        document.getElementById("editModal").style.display = "none";
    }
}




