$(async function () {
    await showUserInfo();
    await getAllUsers();
    await addUser();
})

// show user(R) User and Admin panel
async function showUserInfo() {
    fetch(`/api/user`)
        .then(response => response.json())
        .then(data => {
            document.querySelector('#userName').textContent = data.username;
            document.querySelector('#userRole').textContent = (data.roleSet.map(role => " " + role.roleName.substring(5)).join(' '));
            let user = `$(
                    <tr>
                    <td>${data.id}</td>
                    <td>${data.name}</td>
                    <td>${data.surname}</td>
                    <td>${data.age}</td>
                    <td>${data.email}</td>
                    <td>${data.roleSet.map(role => " " + role.roleName.substring(5))}</td>)</tr>)`;
            $('#userInfo').append(user);
        })
        .catch(error => console.log(error))
}


// admin panel table users(R)
async function getAllUsers() {
    const userTable = $('#AllUsersTable');
    userTable.empty();
    fetch("/api/admin")
        .then(response => response.json())
        .then(data => {
            data.forEach(user => {
                let tmpUserTable = `$(
                        <tr>
                            <td>${user.id}</td>
                            <td>${user.name}</td>
                            <td>${user.surname}</td>
                            <td>${user.age}</td>
                            <td>${user.email}</td>
                            <td>${user.roleSet.map(role => " " + role.roleName.substring(5))}</td>
                            <td>
                                <button type="button" class="btn btn-info" data-bs-toggle="modal"
                                 data-id="${user.id}" data-bs-target="#edit">Edit</button>
                            </td>
                            <td>
                                <button type="button" class="btn btn-danger" data-bs-toggle="modal"
                                 data-id="${user.id}" data-bs-target="#delete">Delete</button>
                            </td>
                        </tr>)`;
                userTable.append(tmpUserTable);
            })
        })
}


//add new user(C)
async function addUser() {
    const rolesResponse = await fetch("/api/admin/roles");
    const roles = await rolesResponse.json();
    roles.forEach(role => {
        let addRoles = document.createElement("option");
        addRoles.value = role.roleId;
        addRoles.text = role.roleName.substring(5);
        document.getElementById('addRoles').appendChild(addRoles);
    });

    const addUserForm = document.forms["addUserForm"];
    const addLink = document.querySelector('#addNewUser');
    const addButton = document.querySelector('#addUserButton');

    addLink.addEventListener('click', (event) => {
        event.preventDefault();
        addUserForm.style.display = 'block';
    });

    addUserForm.addEventListener('submit', addNewUser);
    addButton.addEventListener('click', addNewUser);

    async function addNewUser(e) {
        e.preventDefault();
        let newUserRoles = [];
        for (let i = 0; i < addUserForm.roleSet.options.length; i++) {
            if (addUserForm.roleSet.options[i].selected) {
                newUserRoles.push({
                    roleId: addUserForm.roleSet.options[i].value,
                    roleName:'ROLE_' + addUserForm.roleSet.options[i].text
                });
            }
        }

        const response = await fetch("/api/admin/create", {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                name: addUserForm.name.value,
                surname: addUserForm.surname.value,
                age: addUserForm.age.value,
                email: addUserForm.email.value,
                password: addUserForm.password.value,
                roleSet:  newUserRoles
            })
        });
        addUserForm.reset();
        await getAllUsers()
        $('#nav-home-tab').click();
    }
}

//get user by id
async function getUser(id) {
    let response = await fetch("/api/admin/" + id);
    return await response.json();
}

//edit user(U)
$('#edit').on('show.bs.modal', (ev) => {
    let button = $(ev.relatedTarget);
    let id = button.data('id');
    showEditModal(id);
});

async function showEditModal(id) {

    let user = await getUser(id);
    const form = document.forms["editForm"];

    form.idUpdate.value = user.id;
    form.nameUpdate.value = user.name;
    form.lastnameUpdate.value = user.surname;
    form.ageUpdate.value = user.age;
    form.emailUpdate.value = user.email;
    form.passwordUpdate.value = user.password;

    $('#rolesUpdate').empty();
    fetch("/api/admin/roles")
        .then(response => response.json())
        .then(roles => {
            roles.forEach(role => {
                let el = document.createElement("option");
                el.value = role.roleId;
                el.text = role.roleName.substring(5);
                $('#rolesUpdate')[0].appendChild(el);
            })
        })
}

$('#editUserButton').click(() => {
    updateUser();
});

async function updateUser() {

    const editForm = document.forms["editForm"];
    const id = editForm.idUpdate.value;
    editForm.addEventListener("submit", async (ev) => {
        ev.preventDefault();
        let editUserRoles = [];
        for (let i = 0; i < editForm.rolesUpdate.options.length; i++) {
            if (editForm.rolesUpdate.options[i].selected) {
                editUserRoles.push({
                    roleId: editForm.rolesUpdate.options[i].value,
                    roleName: 'ROLE_' +  editForm.rolesUpdate.options[i].text
                })
            }
        }
        fetch("/api/admin/update", {
            method: "PUT",
            headers: {
                "Content-Type": "application/json",
            },
            body: JSON.stringify({
                id: id,
                name: editForm.nameUpdate.value,
                surname: editForm.lastnameUpdate.value,
                age: editForm.ageUpdate.value,
                email: editForm.emailUpdate.value,
                password: editForm.passwordUpdate.value,
                roleSet: editUserRoles
            }),
        })
            .then(() => {
                $('#editFormCloseButton').click();
                getAllUsers();
            });
    });
}

//delete user(D)
$('#delete').on('show.bs.modal', ev => {
    let button = $(ev.relatedTarget);
    let id = button.data('id');
    showDeleteModal(id);
})

async function showDeleteModal(id) {
    let user = await getUser(id)
    const form = document.forms["deleteForm"];

    form.idDel.value = user.id;
    form.nameDel.value = user.name;
    form.lastnameDel.value = user.surname;
    form.ageDel.value = user.age;
    form.emailDel.value = user.email;

    $('#selectDelRole').empty();
    user.roleSet.forEach(role => {
        let el = document.createElement("option");
        el.text = role.roleName.substring(5);
        el.value = role.id;
        $('#selectDelRole')[0].appendChild(el);
    });
}

$('#deleteUserButton').click(() => {
    deleteUser();
});

async function deleteUser() {

    const deleteForm = document.forms["deleteForm"];
    const id = deleteForm.idDel.value;

    deleteForm.addEventListener("submit", ev => {
        ev.preventDefault();
        fetch("/api/admin/delete/" + id, {
            method: 'DELETE',
            headers: {
                'Content-Type': 'application/json'
            }
        }).then(() => {

            getAllUsers();
            $('#deleteFormCloseButton').click();
        })
    });
}