package taller

import org.scalatest.funsuite.AnyFunSuite
import org.junit.runner.RunWith
import org.scalatestplus.junit.JUnitRunner
import Datos.registro

@RunWith(classOf[JUnitRunner])
class RegistroSismicoTest extends AnyFunSuite {

  val s = new RegistroSismico()
  import s._

  val vacio: List[Sismo] = List()

  // Punto 1 -------------------------------------------------------------------

  test("fuertes: los que superan 5.0, en orden") {
    assert(fuertes(registro, 5.0) == List("Santander", "Choco", "Valle"))
  }

  test("fuertes: con un umbral alto no queda ninguno") {
    assert(fuertes(registro, 9.0) == List())
  }

  test("fuertes: conserva los repetidos") {
    assert(fuertes(registro, 3.0).length == 7)
  }

  test("fuertes: el umbral es estricto") {
    // Hay un sismo de magnitud 6.0 exacta; con umbral 6.0 no debe salir.
    assert(!fuertes(registro, 6.0).contains("Choco"))
  }

  test("enRegion: las magnitudes del Valle") {
    assert(enRegion(registro, "Valle") == List(4.2, 3.8, 5.3))
  }

  test("enRegion: una región que no está da la lista vacía") {
    assert(enRegion(registro, "Amazonas") == List())
  }

  test("enRegion: sobre el registro vacío no hay magnitudes") {
    assert(enRegion(vacio, "Valle") == List())
  }

  test("paresCercanos: con 0.6 solo Valle y Santander") {
    assert(paresCercanos(registro, 0.6) == List(("Valle", "Santander")))
  }

  test("paresCercanos: con 1.0 salen las tres parejas, en orden de aparición") {
    assert(paresCercanos(registro, 1.0) ==
      List(("Valle", "Santander"), ("Valle", "Choco"), ("Santander", "Choco")))
  }

  test("paresCercanos: con 0.1 no hay ninguna") {
    assert(paresCercanos(registro, 0.1) == List())
  }

  test("paresCercanos: cada pareja sale una sola vez") {
    val ps = paresCercanos(registro, 1.0)
    assert(ps.map { case (a, b) => Set(a, b) }.distinct.length == ps.length)
  }

  test("paresCercanos: nunca empareja una región consigo misma") {
    assert(paresCercanos(registro, 5.0).forall { case (a, b) => a != b })
  }

  // Punto 2 -------------------------------------------------------------------

  test("porRegion: agrupa los siete sismos en tres regiones") {
    assert(porRegion(registro).keySet == Set("Valle", "Santander", "Choco"))
    assert(porRegion(registro).values.map(_.length).sum == 7)
  }

  test("conteoPorRegion: tres, dos y dos") {
    assert(conteoPorRegion(registro) ==
      Map("Valle" -> 3, "Santander" -> 2, "Choco" -> 2))
  }

  test("magnitudMaxima: la mayor de cada región") {
    assert(magnitudMaxima(registro) ==
      Map("Valle" -> 5.3, "Santander" -> 5.1, "Choco" -> 6.0))
  }

  test("conteoPorRegion: el registro vacío da el mapa vacío") {
    assert(conteoPorRegion(vacio) == Map())
  }

  test("magnitudMaxima: las regiones ausentes no aparecen") {
    assert(!magnitudMaxima(registro).contains("Amazonas"))
  }

  // Punto 3 -------------------------------------------------------------------

  test("conteoSeguro: una región desconocida responde cero") {
    assert(conteoSeguro(registro)("Amazonas") == 0)
  }

  test("conteoSeguro: una región conocida responde su conteo") {
    assert(conteoSeguro(registro)("Valle") == 3)
  }

  test("conteoSeguro: sobre el registro vacío todo responde cero") {
    assert(conteoSeguro(vacio)("Valle") == 0)
  }

  test("maximaDe: el Chocó llega a 6.0") {
    assert(maximaDe(registro, "Choco") == Some(6.0))
  }

  test("maximaDe: una región sin registros da vacío") {
    assert(maximaDe(registro, "Amazonas") == None)
  }

  test("regionMasActiva: el Valle, con tres sismos") {
    assert(regionMasActiva(registro) == Some("Valle"))
  }

  test("regionMasActiva: sobre el registro vacío da vacío") {
    assert(regionMasActiva(vacio) == None)
  }

  test("regionMasActiva: en empate gana la primera alfabéticamente") {
    val empate = List(
      Sismo(Fecha(2025, 1), "Zulia", 4.0, 10),
      Sismo(Fecha(2025, 1), "Arauca", 4.0, 10))
    assert(regionMasActiva(empate) == Some("Arauca"))
  }

  // Punto 4 -------------------------------------------------------------------

  test("replicas: las primeras cuatro de un sismo de 5.0") {
    assert(replicas(5.0).take(4).toList == List(5.0, 4.0, 3.2, 2.56))
  }

  test("replicas: la secuencia es infinita y no se cuelga") {
    assert(replicas(5.0).take(200).toList.length == 200)
  }

  test("replicas: empieza en la magnitud dada") {
    assert(replicas(7.3).head == 7.3)
  }

  test("replicas: los valores van redondeados a dos decimales") {
    assert(replicas(5.0).take(6).toList.forall(x => x == Math.round(x * 100.0) / 100.0))
  }

  test("replicasSensibles: las que superan 3.0 partiendo de 5.0") {
    assert(replicasSensibles(5.0, 3.0) == List(5.0, 4.0, 3.2))
  }

  test("replicasSensibles: si la primera no supera el mínimo, no hay ninguna") {
    assert(replicasSensibles(2.0, 3.0) == List())
  }

  test("replicasSensibles: el mínimo es estricto") {
    assert(replicasSensibles(3.0, 3.0) == List())
  }

  // Punto 5 -------------------------------------------------------------------

  test("informe: el resumen de las tres regiones") {
    assert(informe(registro, 3.0) ==
      Map("Valle" -> Resumen(3, 5.3, 3),
          "Santander" -> Resumen(2, 5.1, 3),
          "Choco" -> Resumen(2, 6.0, 4)))
  }

  test("informe: el registro vacío da el mapa vacío") {
    assert(informe(vacio, 3.0) == Map())
  }

  test("informe: el conteo coincide con conteoPorRegion") {
    assert(informe(registro, 3.0).map { case (r, res) => (r, res.sismos) } ==
      conteoPorRegion(registro))
  }

  test("informe: la máxima coincide con magnitudMaxima") {
    assert(informe(registro, 3.0).map { case (r, res) => (r, res.maxima) } ==
      magnitudMaxima(registro))
  }

  test("informe: las réplicas coinciden con replicasSensibles del más fuerte") {
    assert(informe(registro, 3.0)("Choco").replicas ==
      replicasSensibles(6.0, 3.0).length)
  }

    // Tests adicionales Punto 3 -----------------------------------------------

  test("conteoSeguro: responde correctamente para varias regiones desconocidas") {
    val datos = List(
      Sismo(Fecha(2026, 1), "Cauca", 4.0, 20),
      Sismo(Fecha(2026, 2), "Huila", 5.0, 30)
    )

    val consulta = conteoSeguro(datos)

    assert(consulta("Amazonas") == 0)
    assert(consulta("Bogota") == 0)
    assert(consulta("Cesar") == 0)
  }

  test("maximaDe: encuentra la máxima sin importar el orden de los registros") {
    val datos = List(
      Sismo(Fecha(2026, 1), "Nariño", 3.0, 20),
      Sismo(Fecha(2026, 2), "Nariño", 8.2, 30),
      Sismo(Fecha(2026, 3), "Nariño", 5.0, 25)
    )

    assert(maximaDe(datos, "Nariño") == Some(8.2))
  }

  test("maximaDe: una región con un solo sismo devuelve su magnitud") {
    val datos = List(
      Sismo(Fecha(2026, 1), "Caldas", 4.7, 20),
      Sismo(Fecha(2026, 2), "Tolima", 6.3, 30)
    )

    assert(maximaDe(datos, "Caldas") == Some(4.7))
  }

  test("regionMasActiva: entre tres regiones empatadas gana la alfabéticamente menor") {
    val datos = List(
      Sismo(Fecha(2026, 1), "Zulia", 4.0, 20),
      Sismo(Fecha(2026, 2), "Boyaca", 5.0, 30),
      Sismo(Fecha(2026, 3), "Arauca", 4.5, 25)
    )

    assert(regionMasActiva(datos) == Some("Arauca"))
  }

  test("regionMasActiva: una región con más registros gana aunque aparezca al final") {
    val datos = List(
      Sismo(Fecha(2026, 1), "Antioquia", 4.0, 20),
      Sismo(Fecha(2026, 2), "Cauca", 5.0, 30),
      Sismo(Fecha(2026, 3), "Cauca", 4.5, 25),
      Sismo(Fecha(2026, 4), "Cauca", 3.5, 40)
    )

    assert(regionMasActiva(datos) == Some("Cauca"))
  }

    // Tests adicionales Punto 4 -----------------------------------------------

  test("replicas: el redondeo acumulado se utiliza en cada nueva réplica") {
    assert(replicas(1.11).take(5).toList ==
      List(1.11, 0.89, 0.71, 0.57, 0.46))
  }

  test("replicas: permite solicitar una cantidad grande de elementos") {
    assert(replicas(8.0).take(1000).toList.length == 1000)
  }

  test("replicas: una magnitud inicial de cero mantiene la secuencia en cero") {
    assert(replicas(0.0).take(10).toList.forall(_ == 0.0))
  }

  test("replicasSensibles: incluye las réplicas que superan el mínimo") {
    assert(replicasSensibles(4.0, 2.0) ==
      List(4.0, 3.2, 2.56, 2.05))
  }

  test("replicasSensibles: termina al alcanzar una magnitud igual al mínimo") {
    assert(replicasSensibles(3.13, 2.0) ==
      List(3.13, 2.5))
  }

    // Tests adicionales Punto 5 -----------------------------------------------

  test("informe: calcula correctamente regiones con diferentes cantidades de sismos") {
    val datos = List(
      Sismo(Fecha(2026, 1), "Huila", 4.5, 20),
      Sismo(Fecha(2026, 2), "Huila", 3.0, 30),
      Sismo(Fecha(2026, 3), "Caldas", 6.0, 25),
      Sismo(Fecha(2026, 4), "Huila", 5.0, 40)
    )

    assert(informe(datos, 4.0) ==
      Map(
        "Huila" -> Resumen(3, 5.0, 1),
        "Caldas" -> Resumen(1, 6.0, 4)
      ))
  }

  test("informe: conserva el conteo y la máxima con un mínimo muy alto") {
    val datos = List(
      Sismo(Fecha(2026, 1), "Cauca", 4.0, 20),
      Sismo(Fecha(2026, 2), "Cauca", 5.5, 30),
      Sismo(Fecha(2026, 3), "Valle", 3.5, 25)
    )

    assert(informe(datos, 10.0) ==
      Map(
        "Cauca" -> Resumen(2, 5.5, 0),
        "Valle" -> Resumen(1, 3.5, 0)
      ))
  }

  test("informe: una región con un solo sismo genera un resumen completo") {
    val datos = List(
      Sismo(Fecha(2026, 1), "Nariño", 4.0, 35)
    )

    assert(informe(datos, 2.0) ==
      Map("Nariño" -> Resumen(1, 4.0, 3)))
  }

  test("informe: cambiar el orden del registro no cambia los resúmenes") {
    val datos = List(
      Sismo(Fecha(2026, 1), "Meta", 4.0, 20),
      Sismo(Fecha(2026, 2), "Huila", 6.0, 30),
      Sismo(Fecha(2026, 3), "Meta", 5.0, 25),
      Sismo(Fecha(2026, 4), "Huila", 3.0, 40)
    )

    assert(informe(datos, 2.0) == informe(datos.reverse, 2.0))
  }

  test("informe: la suma de los conteos coincide con el tamaño del registro") {
    val datos = List(
      Sismo(Fecha(2026, 1), "Cauca", 4.0, 20),
      Sismo(Fecha(2026, 2), "Huila", 5.0, 30),
      Sismo(Fecha(2026, 3), "Cauca", 6.0, 25),
      Sismo(Fecha(2026, 4), "Meta", 3.0, 40),
      Sismo(Fecha(2026, 5), "Huila", 4.5, 35)
    )

    assert(informe(datos, 2.0).values.map(_.sismos).sum == datos.length)
  }
}
