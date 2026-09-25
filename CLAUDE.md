# CLAUDE.md

## Project: State Graph Search Visualization

A single-file, front-end web visualization of Java BFS/DFS state graph search algorithms, rendering a geographic US map where states light up as they are visited.

### Architecture

**Single-file SPA** — `index.html` contains all HTML, CSS, and JavaScript. No build step, no bundler, no server.

**Libraries loaded at runtime from CDN:**
- D3.js v7 (geo projections, SVG rendering, selection/transition)
- TopoJSON client 3 (us-atlas state boundary data)

### SVG Layer Architecture

Five explicit `<g>` containers appended to a root group, DOM order = z-index:

1. **`#layer-fills`** — State polygon fills (`fillPaths`), `stroke: none`
2. **`#layer-strokes`** — State borders (`strokePaths`), `fill: none`
3. **`#layer-hover`** — Reserved for future hover clone overlays
4. **`#layer-outlines`** — Start/end state dashed outlines
5. **`#layer-labels`** — State abbreviation text labels

Fills and strokes are **separate selections** so that the stroke layer never renders below a neighboring state's fill. The `.raise()` method moves individual elements to the top of their group on interaction.

### Key Design Decisions

- **Interleaved algorithm + animation** — `step()` uses `async/await` with `await delay(ms)` so each algorithm step is visible on screen. No pre-compute-then-animate approach.
- **`prevMap` (Map) instead of string properties** — `prevMap.set(nbCode, curr)` tracks predecessors like Java's `State.setPrev()`. String primitive property assignment is unreliable in JS.
- **Force instant during search** — `applyStateStyles(true)` and `updateLabels(true)` skip D3 transitions during search (`isRunning === true`) to avoid transition queue buildup and browser freeze. Transitions only resume after search completes for path highlighting.
- **GeoPath rendering** — `d3.geoPath()` generates SVG path data strings (not selections). The selection object holds the `<path>` elements; we transition *those*, not `geoPathFn` itself.
- **State label centroids** — Raw geometric centroids land in water for non-convex states (FL peninsula, MI's north/south peninsulas, LA's delta). A `LABEL_OFFSETS` map applies pixel offsets to center labels in readable positions.
- **Label legibility** — CSS `paint-order: stroke fill` with a dark stroke halo ensures labels remain readable against any fill color (dark base, bright blue visited, orange current, green path).

### Search Algorithm (mirrors Java implementation)

```
Seed: visitedSet.add(start), discoveredSet.add(start)

Loop:
  Pop current from stack (DFS) or queue (BFS)
  Mark current as currentNode (orange)
  For each unvisited neighbor:
    visitedSet.add(neighbor), discoveredSet.add(neighbor)
    prevMap.set(neighbor, current)  // ← like Java's neighbor.setPrev(curr)
    Push neighbor to stack/queue
    If neighbor == end: backtrack via prevMap, render path

Path backtrack (buildSolution):
  Walk prevMap from end to start → reverse → green path highlight
```

### Files

- `index.html` — Complete visualization (all code)
- `../StateNeighbors/src/UnitedStates.java` — Original Java implementation (reference)
- `../StateNeighbors/src/State.java` — Original Java State class (reference)
- `../StateNeighbors/contiguous-usa.txt` — Adjacency data (embedded in index.html)

### Known constraints

- Contiguous US only (AK/HI excluded — no adjacency edges in the data set)
- `geoPathFn.centroid()` and `geoPathFn.bounds()` return NaN for AK/HI under the Albers USA projection when fit to contiguous features
- Browser-only — no server, no Node.js, no build pipeline
