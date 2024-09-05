install openldap , refer https://www.linuxtechi.com/setup-openldap-server-on-ubuntu/
Ldap123!.
Refer src/main/resources/base-groups.ldif



```sh
sudo apt install slapd ldap-utils
sudo dpkg-reconfigure slapd
systemctl restart slapd
systemctl status slapd

ldapadd -x -D cn=admin,dc=balajikrishnan,dc=com -W -f base-groups.ldif
ldapsearch -Q -LLL -Y EXTERNAL -H ldapi:///
ldapadd -x -D cn=admin,dc=balajikrishnan,dc=com -W -f admin-group.ldif
ldapsearch -x -LLL -b dc=balajikrishnan,dc=com '(cn=admin)' gidNumber

slappasswd
ldapadd -x -D cn=admin,dc=linuxtechi,dc=com -W -f user.ldif
ldapsearch -x -LLL -b dc=balajikrishnan,dc=com '(uid=alex)' cn uidNumber gidNumber
```

base-groups.ldif
```ldif
dn: ou=people,dc=balajikrishnan,dc=com
objectClass: organizationalUnit
ou: people

dn: ou=groups,dc=balajikrishnan,dc=com
objectClass: organizationalUnit
ou: groups
```

admin-group.ldif
```ldif
dn: cn=admin,ou=groups,dc=balajikrishnan,dc=com
objectClass: posixGroup
cn: admin
gidNumber: 5000
```
