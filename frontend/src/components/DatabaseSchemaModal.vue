<script>
import BaseModal from '@/components/modal/BaseModal.vue'
import { SCHEMA_FOREIGN_KEYS, SCHEMA_GROUPS, SCHEMA_TABLES } from '@/databaseSchema.js'

const TABLE_WIDTH = 260
const COLUMN_GAP = 140
const HEADER_HEIGHT = 30
const ROW_HEIGHT = 20
const TABLE_GAP = 28
const PADDING = 20
const GROUP_LABEL_HEIGHT = 28
// Seosejoone horisontaalne algus/lõpp tabeli servas, kuhu mahub crow's foot või || märk
const LINE_STUB = 26
const CORNER_RADIUS = 6

// Täisnurkne murdjoon ümardatud nurkadega (nagu andmemudeli tööriistades)
function createRoundedPath(points) {
  const uniquePoints = points.filter(
    (point, index) =>
      index === 0 || point[0] !== points[index - 1][0] || point[1] !== points[index - 1][1],
  )
  let path = `M ${uniquePoints[0][0]} ${uniquePoints[0][1]}`
  for (let index = 1; index < uniquePoints.length - 1; index++) {
    const [previousX, previousY] = uniquePoints[index - 1]
    const [x, y] = uniquePoints[index]
    const [nextX, nextY] = uniquePoints[index + 1]
    const radius = Math.min(
      CORNER_RADIUS,
      Math.hypot(x - previousX, y - previousY) / 2,
      Math.hypot(nextX - x, nextY - y) / 2,
    )
    const beforeX = x - Math.sign(x - previousX) * radius
    const beforeY = y - Math.sign(y - previousY) * radius
    const afterX = x + Math.sign(nextX - x) * radius
    const afterY = y + Math.sign(nextY - y) * radius
    path += ` L ${beforeX} ${beforeY} Q ${x} ${y} ${afterX} ${afterY}`
  }
  const [lastX, lastY] = uniquePoints[uniquePoints.length - 1]
  return path + ` L ${lastX} ${lastY}`
}

// Crow's foot (FK pool, "mitu"): kolm haru tabeli servani + kriips. direction näitab tabelist eemale.
function createManyMark(x, y, direction) {
  const tipX = x + direction * 12
  const barX = x + direction * 16
  return (
    ` M ${tipX} ${y} L ${x} ${y - 6} M ${tipX} ${y} L ${x} ${y + 6}` +
    ` M ${barX} ${y - 6} L ${barX} ${y + 6}`
  )
}

// Kaks kriipsu (viidatav pool, "täpselt üks")
function createOneMark(x, y, direction) {
  const firstBarX = x + direction * 8
  const secondBarX = x + direction * 12
  return (
    ` M ${firstBarX} ${y - 6} L ${firstBarX} ${y + 6}` +
    ` M ${secondBarX} ${y - 6} L ${secondBarX} ${y + 6}`
  )
}

export default {
  name: 'DatabaseSchemaModal',
  components: { BaseModal },
  props: {
    isOpen: Boolean,
  },
  emits: ['event-close'],
  data() {
    return {
      isOnlyKeysShown: false,
      hoveredTableName: null,
      selectedTableName: null,
    }
  },
  computed: {
    foreignKeyColumns() {
      return new Set(
        SCHEMA_FOREIGN_KEYS.map(([tableName, columnName]) => tableName + '.' + columnName),
      )
    },

    // Arvutab iga tabeli asukoha: tabelid paigutatakse grupi kaupa veergudesse üksteise alla
    tables() {
      const nextYByGroup = SCHEMA_GROUPS.map(() => PADDING + GROUP_LABEL_HEIGHT)
      return SCHEMA_TABLES.map((table) => {
        const columns = table.columns
          .map(([name, type]) => ({
            name,
            type,
            isPrimaryKey: name === 'id',
            isForeignKey: this.foreignKeyColumns.has(table.name + '.' + name),
          }))
          .filter((column) => !this.isOnlyKeysShown || column.isPrimaryKey || column.isForeignKey)
        const x = PADDING + table.group * (TABLE_WIDTH + COLUMN_GAP)
        const y = nextYByGroup[table.group]
        const height = HEADER_HEIGHT + columns.length * ROW_HEIGHT + 6
        nextYByGroup[table.group] = y + height + TABLE_GAP
        return { name: table.name, group: table.group, columns, x, y, height }
      })
    },

    tablesByName() {
      return Object.fromEntries(this.tables.map((table) => [table.name, table]))
    },

    groupLabels() {
      return SCHEMA_GROUPS.map((group) => ({
        label: group.label,
        x: PADDING + group.index * (TABLE_WIDTH + COLUMN_GAP),
        y: PADDING + 12,
      }))
    },

    // Paremale jääb terve vahe, sest viimase veeru sisesed seosed käivad tabelitest paremalt
    svgWidth() {
      return PADDING + SCHEMA_GROUPS.length * (TABLE_WIDTH + COLUMN_GAP)
    },

    svgHeight() {
      return Math.max(...this.tables.map((table) => table.y + table.height)) + PADDING
    },

    activeTableName() {
      return this.hoveredTableName ?? this.selectedTableName
    },

    // Iga seose vertikaalne lõik käib kahe veeru vahelises vahes. Samasse vahesse jäävad
    // jooned jaotatakse eri radadele, et need üksteist ei kataks.
    relations() {
      const relations = SCHEMA_FOREIGN_KEYS.map(([tableName, columnName, referencedTableName]) => {
        const table = this.tablesByName[tableName]
        const referencedTable = this.tablesByName[referencedTableName]
        const isSameGroup = table.group === referencedTable.group
        const isTargetOnRight = referencedTable.group > table.group
        return {
          key: tableName + '.' + columnName,
          table,
          referencedTable,
          startY: this.rowCenterY(table, columnName),
          endY: this.rowCenterY(referencedTable, 'id'),
          isSameGroup,
          isTargetOnRight,
          gapIndex: isTargetOnRight ? referencedTable.group - 1 : referencedTable.group,
          isActive:
            tableName === this.activeTableName || referencedTableName === this.activeTableName,
        }
      })

      SCHEMA_GROUPS.forEach((group) => {
        const gapRelations = relations
          .filter((relation) => relation.gapIndex === group.index)
          .sort((first, second) => first.endY - second.endY || first.startY - second.startY)
        const gapStartX = PADDING + group.index * (TABLE_WIDTH + COLUMN_GAP) + TABLE_WIDTH
        const laneSpace = COLUMN_GAP - LINE_STUB * 2
        gapRelations.forEach((relation, index) => {
          relation.laneX = gapStartX + LINE_STUB + ((index + 0.5) * laneSpace) / gapRelations.length
        })
      })

      return relations.map((relation) => ({
        key: relation.key,
        path: this.createRelationPath(relation),
        isActive: relation.isActive,
      }))
    },

    relatedTableNames() {
      if (!this.activeTableName) {
        return null
      }
      const tableNames = new Set([this.activeTableName])
      SCHEMA_FOREIGN_KEYS.forEach(([tableName, , referencedTableName]) => {
        if (tableName === this.activeTableName) tableNames.add(referencedTableName)
        if (referencedTableName === this.activeTableName) tableNames.add(tableName)
      })
      return tableNames
    },
  },
  methods: {
    rowCenterY(table, columnName) {
      const rowIndex = table.columns.findIndex((column) => column.name === columnName)
      return table.y + HEADER_HEIGHT + rowIndex * ROW_HEIGHT + ROW_HEIGHT / 2 + 3
    },

    // Joon algab FK veeru realt (crow's foot) ja lõpeb viidatud tabeli id real (||).
    // Sama veeru tabelite vahel (ka viide iseendale) käib joon tabelitest paremalt.
    createRelationPath(relation) {
      const { table, referencedTable, startY, endY, laneX } = relation
      let startX
      let endX
      let startDirection
      let endDirection
      if (relation.isSameGroup) {
        startX = table.x + TABLE_WIDTH
        endX = referencedTable.x + TABLE_WIDTH
        startDirection = 1
        endDirection = 1
      } else if (relation.isTargetOnRight) {
        startX = table.x + TABLE_WIDTH
        endX = referencedTable.x
        startDirection = 1
        endDirection = -1
      } else {
        startX = table.x
        endX = referencedTable.x + TABLE_WIDTH
        startDirection = -1
        endDirection = 1
      }
      const line = createRoundedPath([
        [startX, startY],
        [laneX, startY],
        [laneX, endY],
        [endX, endY],
      ])
      return (
        line +
        createManyMark(startX, startY, startDirection) +
        createOneMark(endX, endY, endDirection)
      )
    },

    isTableRelated(tableName) {
      return this.relatedTableNames !== null && this.relatedTableNames.has(tableName)
    },

    // Klõps lukustab valiku (vajalik ka puutetundliku ekraani jaoks), teine klõps vabastab
    toggleSelectedTable(tableName) {
      this.selectedTableName = this.selectedTableName === tableName ? null : tableName
    },

    close() {
      this.hoveredTableName = null
      this.selectedTableName = null
      this.$emit('event-close')
    },
  },
  TABLE_WIDTH,
  HEADER_HEIGHT,
  ROW_HEIGHT,
}
</script>

<template>
  <BaseModal :is-open="isOpen" size="xl" @event-modal-closed="close">
    <template #title>Andmebaasi skeem</template>
    <template #body>
      <div class="d-flex flex-wrap align-items-center justify-content-between gap-2 mb-3">
        <small class="text-body-secondary">
          {{ tables.length }} tabelit · {{ relations.length }} seost. Vii hiir tabelile või klõpsa
          sellel, et näha tabeli seoseid.
        </small>
        <div class="form-check form-switch mb-0">
          <input
            id="schema-only-keys"
            v-model="isOnlyKeysShown"
            class="form-check-input"
            type="checkbox"
          />
          <label class="form-check-label" for="schema-only-keys">Näita ainult võtmeid</label>
        </div>
      </div>

      <div class="schema-scroll rounded-3">
        <svg
          :width="svgWidth"
          :height="svgHeight"
          :viewBox="`0 0 ${svgWidth} ${svgHeight}`"
          class="schema-svg"
          role="img"
          aria-label="Andmebaasi tabelid ja nendevahelised seosed"
        >
          <text
            v-for="groupLabel in groupLabels"
            :key="groupLabel.label"
            :x="groupLabel.x"
            :y="groupLabel.y"
            class="schema-group-label"
          >
            {{ groupLabel.label }}
          </text>

          <!-- Mitteaktiivsed seosed on tabelite all, aktiivsed peal, et neid saaks jälgida -->
          <path
            v-for="relation in relations.filter((relation) => !relation.isActive)"
            :key="relation.key"
            :d="relation.path"
            class="schema-relation"
            :class="{ 'schema-relation-dimmed': activeTableName }"
          />

          <g
            v-for="table in tables"
            :key="table.name"
            class="schema-table"
            :class="{
              'schema-table-related': isTableRelated(table.name),
              'schema-table-active': table.name === activeTableName,
            }"
            @mouseenter="hoveredTableName = table.name"
            @mouseleave="hoveredTableName = null"
            @click="toggleSelectedTable(table.name)"
          >
            <rect
              :x="table.x"
              :y="table.y"
              :width="$options.TABLE_WIDTH"
              :height="table.height"
              rx="8"
              class="schema-table-body"
            />
            <path
              :d="`M ${table.x} ${table.y + $options.HEADER_HEIGHT} v ${-$options.HEADER_HEIGHT + 8} a 8 8 0 0 1 8 -8 h ${$options.TABLE_WIDTH - 16} a 8 8 0 0 1 8 8 v ${$options.HEADER_HEIGHT - 8} z`"
              class="schema-table-header"
            />
            <text :x="table.x + 12" :y="table.y + 20" class="schema-table-name">
              {{ table.name }}
            </text>

            <g v-for="(column, columnIndex) in table.columns" :key="column.name">
              <text
                v-if="column.isPrimaryKey || column.isForeignKey"
                :x="table.x + 10"
                :y="table.y + $options.HEADER_HEIGHT + columnIndex * $options.ROW_HEIGHT + 17"
                :class="column.isPrimaryKey ? 'schema-key-pk' : 'schema-key-fk'"
              >
                {{ column.isPrimaryKey ? 'PK' : 'FK' }}
              </text>
              <text
                :x="table.x + 34"
                :y="table.y + $options.HEADER_HEIGHT + columnIndex * $options.ROW_HEIGHT + 17"
                class="schema-column-name"
                :class="{ 'fw-semibold': column.isPrimaryKey }"
              >
                {{ column.name }}
              </text>
              <text
                :x="table.x + $options.TABLE_WIDTH - 10"
                :y="table.y + $options.HEADER_HEIGHT + columnIndex * $options.ROW_HEIGHT + 17"
                class="schema-column-type"
                text-anchor="end"
              >
                {{ column.type }}
              </text>
            </g>
          </g>

          <path
            v-for="relation in relations.filter((relation) => relation.isActive)"
            :key="relation.key"
            :d="relation.path"
            class="schema-relation-active"
          />
        </svg>
      </div>

      <div class="d-flex flex-wrap gap-3 mt-2 small text-body-secondary">
        <span><span class="schema-legend-pk">PK</span> primaarvõti</span>
        <span><span class="schema-legend-fk">FK</span> välisvõti</span>
        <span class="d-inline-flex align-items-center gap-1">
          <svg width="26" height="14" viewBox="0 0 26 14" class="schema-legend-line">
            <path d="M 0 7 L 26 7 M 12 7 L 0 1 M 12 7 L 0 13 M 16 1 L 16 13" />
          </svg>
          mitu (välisvõtmega tabel)
        </span>
        <span class="d-inline-flex align-items-center gap-1">
          <svg width="26" height="14" viewBox="0 0 26 14" class="schema-legend-line">
            <path d="M 0 7 L 26 7 M 14 1 L 14 13 M 18 1 L 18 13" />
          </svg>
          üks (viidatav tabel)
        </span>
      </div>
    </template>
  </BaseModal>
</template>

<style scoped>
.schema-scroll {
  --schema-table-border: var(--bs-gray-400);
  --schema-line: var(--bs-gray-500);
  --schema-line-active: color-mix(in srgb, var(--brand-green) 65%, var(--bs-gray-600));

  overflow: auto;
  max-height: 70vh;
  background-color: var(--bs-tertiary-bg);
  border: 1px solid var(--bs-border-color);
}

.schema-svg {
  display: block;
  font-size: 11.5px;
}

.schema-group-label {
  font-size: 11px;
  font-weight: 700;
  letter-spacing: 0.06em;
  text-transform: uppercase;
  fill: var(--bs-secondary-color);
}

.schema-table {
  cursor: pointer;
}

/* Tabelid on vaikimisi hallid; hoveri ajal saavad seotud tabelid kerge rohekashalli tooni */
.schema-table-body {
  fill: var(--bs-body-bg);
  stroke: var(--schema-table-border);
  transition:
    fill 0.15s ease,
    stroke 0.15s ease;
}

.schema-table-header {
  fill: var(--bs-secondary-bg);
  transition: fill 0.15s ease;
}

.schema-table-related .schema-table-body {
  fill: color-mix(in srgb, var(--brand-green) 4%, var(--bs-body-bg));
  stroke: color-mix(in srgb, var(--brand-green) 30%, var(--schema-table-border));
}

.schema-table-related .schema-table-header {
  fill: color-mix(in srgb, var(--brand-green) 12%, var(--bs-secondary-bg));
}

.schema-table-active .schema-table-header {
  fill: color-mix(in srgb, var(--brand-green) 22%, var(--bs-secondary-bg));
}

.schema-table-name {
  fill: var(--bs-body-color);
  font-size: 13px;
  font-weight: 700;
}

.schema-column-name {
  fill: var(--bs-body-color);
}

.schema-column-type {
  fill: var(--bs-secondary-color);
  font-family: var(--bs-font-monospace);
  font-size: 10.5px;
}

.schema-key-pk,
.schema-key-fk {
  font-size: 9px;
  font-weight: 700;
  fill: var(--bs-secondary-color);
}

.schema-key-pk {
  fill: var(--bs-body-color);
}

.schema-legend-pk,
.schema-legend-fk {
  font-weight: 700;
}

.schema-relation,
.schema-relation-active,
.schema-legend-line path {
  fill: none;
  stroke-linecap: round;
  stroke-linejoin: round;
}

.schema-relation {
  stroke: var(--schema-line);
  stroke-width: 1.1;
  transition: opacity 0.15s ease;
}

.schema-relation-dimmed {
  opacity: 0.3;
}

.schema-relation-active {
  stroke: var(--schema-line-active);
  stroke-width: 1.6;
  pointer-events: none;
}

.schema-legend-line path {
  stroke: var(--schema-line);
  stroke-width: 1.3;
}
</style>
