# UsersView — Balsamiq märkmed

## Vaate märkmed

```text
Roll: Admin, Haldur
Failinimi: UsersView.vue
Frontend rada: /users

Vaatega seotud lisainfo:
Vaate avamisel küsitakse backendilt GET /api/users sõnumiga kõik süsteemi kasutajad (sh kustutatud) ja kuvatakse tabelis. Veerus "Nimi" kuvatakse eesnimi ja perenimi koos (firstName + " " + lastName, nt "Anna Admin"). roleName kuvatakse eesti keeles (ADMIN → Admin, MANAGER → Haldur, USER → Kasutaja), status samuti (A → Aktiivne, P → Ootel, I → Kustutatud).

Prügikasti ikoonile vajutades saadetakse backendile DELETE /api/users/{userId} sõnum ning seejärel laaditakse tabel uuesti (rida jääb nimekirja staatusega "Kustutatud").

Pliiatsi ikoonile vajutades suunatakse kasutaja muutmise vaatele /user?userId={id}. Nupule "+ Lisa uus" vajutades suunatakse kasutaja vaatele /user (ilma userId-ta). Kumbki ei tee selles vaates API kutset.
```

## API märkmed — GET /api/users

```text
API: GET /api/users

UserResponse.java
Response (200):
[
  {
    "userId": 1,
    "firstName": "Anna",
    "lastName": "Admin",
    "email": "admin@example.com",
    "groupName": "Frontend Team",
    "roleName": "ADMIN",
    "status": "A"
  },
  {
    "userId": 2,
    "firstName": "Marko",
    "lastName": "Manager",
    "email": "manager@example.com",
    "groupName": null,
    "roleName": "MANAGER",
    "status": "A"
  }
]

API teenuse lisainfo:
Tagastatakse kõik kasutajad sõltumata staatusest (A = aktiivne, P = ootel, I = kustutatud). Kasutaja kuulub maksimaalselt ühte gruppi (group_member); kui grupp puudub, on groupName null. Ootel kasutajal (registreerimine lõpetamata, profile puudub) on firstName ja lastName null.

Veateated: —
```

## API märkmed — DELETE /api/users/{userId}

```text
API: DELETE /api/users/{userId}

Response (200): NONE

API teenuse lisainfo:
Soft delete — kasutajat andmebaasist ei eemaldata, backend muudab user tabelis status väärtuseks 'I'.

Veateated:
HTTP: 404
errorCode: PRIMARY_KEY_NOT_FOUND
message: "Ei leidnud primary keyd 'userId' väärtusega: 123"
```
