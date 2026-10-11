#!/bin/sh
# Genera CHANGELOG.md desde los commits: una seccion por version (tag) y, dentro, un grupo por tipo de commit.
# Los commits que no siguen Conventional Commits (anteriores al hook) no aparecen.
# Uso: sh scripts/generar-changelog.sh [version]   (ej. v3.0.0 al preparar una release)

titulo() {
    case "$1" in
        feat) echo "Funcionalidades" ;;
        fix) echo "Correcciones" ;;
        refactor) echo "Refactorizaciones" ;;
        test) echo "Pruebas" ;;
        docs) echo "Documentacion" ;;
        build) echo "Build" ;;
        *) echo "Mantenimiento" ;;
    esac
}

# $1 = rango de git log, $2 = nombre de la seccion
seccion() {
    echo "## $2"
    echo
    for tipo in feat fix refactor test docs build chore; do
        lineas=$(git log --no-merges --format='%s' $1 | grep -E "^$tipo(\([^)]*\))?!?: " | sed -E "s/^$tipo(\([^)]*\))?!?: /- /")
        if [ -n "$lineas" ]; then
            echo "### $(titulo "$tipo")"
            echo "$lineas"
            echo
        fi
    done
}

{
    echo "# Changelog"
    echo
    echo "Generado automaticamente con \`scripts/generar-changelog.sh\` a partir de los commits (Conventional Commits)."
    echo
    anterior=HEAD
    nombre="${1:-Sin publicar}"
    for tag in $(git tag --sort=-creatordate); do
        seccion "$tag..$anterior" "$nombre"
        anterior=$tag
        nombre=$tag
    done
    seccion "$anterior" "$nombre"
} > CHANGELOG.md

echo "CHANGELOG.md actualizado"
