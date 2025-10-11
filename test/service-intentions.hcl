kind = "service-intentions"
name = "short-link-key-service"
sources = [
  {
    name   = "shortlink-manager-service"
    action = "allow"
  }
]

---

kind = "service-intentions"
name = "sks"
sources = [
  {
    name   = "shortlink-manager-service"
    action = "allow"
  },
  {
    name   = "sms"  # Permitir via alias também
    action = "allow"
  }
]

---

kind = "service-intentions"
name = "shortlink-manager-service"
sources = [
  {
    name   = "redirect-service"
    action = "allow"
  }
]

---

kind = "service-intentions"
name = "sms"
sources = [
  {
    name   = "redirect-service"
    action = "allow"
  }
]
