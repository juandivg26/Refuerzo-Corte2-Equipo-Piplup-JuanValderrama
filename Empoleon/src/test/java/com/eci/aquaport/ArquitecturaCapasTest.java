package com.eci.aquaport;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.fields;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static com.tngtech.archunit.library.Architectures.layeredArchitecture;

/**
 * Auditoria automatica de la arquitectura por capas (Empoleon reto 14).
 * Se ejecuta con mvn test: si alguien rompe la direccion de las dependencias, el build falla.
 */
@AnalyzeClasses(packages = "com.eci.aquaport", importOptions = ImportOption.DoNotIncludeTests.class)
class ArquitecturaCapasTest {

    @ArchTest
    static final ArchRule capasRespetanLaDireccionDeDependencias = layeredArchitecture()
            .consideringOnlyDependenciesInLayers()
            .layer("Dominio").definedBy("..dominio..")
            .layer("Aplicacion").definedBy("..aplicacion..")
            .layer("Infraestructura").definedBy("..infraestructura..")
            .whereLayer("Infraestructura").mayNotBeAccessedByAnyLayer()
            .whereLayer("Aplicacion").mayOnlyBeAccessedByLayers("Infraestructura")
            .whereLayer("Dominio").mayOnlyBeAccessedByLayers("Aplicacion", "Infraestructura")
            .because("la infraestructura depende de aplicacion y dominio; el dominio no depende de nadie");

    @ArchTest
    static final ArchRule dominioSoloJavaPuro = classes()
            .that().resideInAPackage("..dominio..")
            .should().onlyDependOnClassesThat().resideInAnyPackage("..dominio..", "java..")
            .because("el dominio debe poder ejecutarse sin ninguna libreria externa");

    @ArchTest
    static final ArchRule dominioNoCreaInfraestructura = noClasses()
            .that().resideInAPackage("..dominio..")
            .should().dependOnClassesThat().resideInAPackage("..infraestructura..")
            .because("no puede haber un new ClaseInfraestructura() dentro del dominio");

    @ArchTest
    static final ArchRule serviciosConInyeccionPorConstructor = fields()
            .that().areDeclaredInClassesThat().resideInAPackage("..aplicacion..")
            .should().beFinal()
            .because("los servicios reciben sus dependencias por constructor, no por campo ni setter");

    @ArchTest
    static final ArchRule adaptadoresImplementanPuertosDelDominio = classes()
            .that().resideInAPackage("..infraestructura..").and().haveSimpleNameStartingWith("Adaptador")
            .should().dependOnClassesThat().resideInAPackage("..dominio..")
            .because("un adaptador existe para implementar un puerto definido en el dominio");
}
