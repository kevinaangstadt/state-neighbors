# CLAUDE.md

## Project: State Graph Search Visualization

A single-file, front-end web visualization of Java BFS/DFS state graph search algorithms, rendering a geographic US map where states light up as they are visited.

### Architecture

**Single-file SPA** — `index.html` contains all HTML, CSS, and JavaScript. No build step, no bundler, no server.

**Libraries loaded at runtime from CDN:**
- D3.js v7 (geo projections, SVG rendering, selection/transition)
- TopoJSON client 3 (us-atlas state boundary data)

### SVG Layer Architecture

Six explicit `<g>` containers appended to a root group, DOM order = z-index (bottom → top):

1. **`#layer-fills`** — State polygon fills (`fillPaths`), `stroke: none`
2. **`#layer-strokes`** — State borders (`strokePaths`), `fill: none`
3. **`#layer-hover`** — Reserved for future hover clone overlays
4. **`#layer-outlines`** — Start/end state dashed outlines
5. **`#layer-arrows`** — Search arrows (exploratory grey + path solution white)
6. **`#layer-labels`** — State abbreviation text labels

Fills and strokes are **separate selections** so that the stroke layer never renders below a neighboring state's fill. The `.raise()` method moves individual elements to the top of their group on interaction.

### Arrow System

**Two marker definitions** in `<defs>`:
- `#arrowhead` — grey (`#94a3b8`), used for exploratory arrows during search
- `#arrowhead-path` — white (`#ffffff`), used for final solution path arrows

**Exploratory arrows** — drawn in `drawSearchArrow(nbCode, curr)` as each neighbor is discovered. Points from discovered state → predecessor (backwards along solution chain). Grey, 2px stroke, 70% opacity. Uses `getLabelCoordinates()` for consistent positioning with state text labels, with 14px shortening at the target end so the arrowhead stops cleanly outside the label.

**Solution path arrows** — flipped during `highlightPathVisual()` via `highlightPathArrow(fromCode, toCode)`:
1. Removes the old backward exploratory arrow (edgeKey = `fromCode->toCode`)
2. Draws a new forward-pointing arrow (toCode → fromCode) in white, 3.5px stroke, 100% opacity
3. Raises it above remaining exploratory arrows

**Layer order maintenance** — `updateLabels()` calls `gArrows.raise()` before `gLabels.raise()` to maintain correct z-index stack.

### Key Design Decisions

- **Interleaved algorithm + animation** — `step()` uses `async/await` with `await delay(ms)` so each algorithm step is visible on screen. No pre-compute-then-animate approach.
- **`prevMap` (Map) instead of string properties** — `prevMap.set(nbCode, curr)` tracks predecessors like Java's `State.setPrev()`. String primitive property assignment is unreliable in JS.
- **Force instant during search** — `applyStateStyles(true)` and `updateLabels(true)` skip D3 transitions during search (`isRunning === true`) to avoid transition queue buildup and browser freeze. Transitions only resume after search completes for path highlighting.
- **GeoPath rendering** — `d3.geoPath()` generates SVG path data strings (not selections). The selection object holds the `<path>` elements; we transition *those*, not `geoPathFn` itself.
- **State label centroids** — Raw geometric centroids land in water for non-convex states (FL peninsula, MI's north/south peninsulas, LA's delta). A `LABEL_OFFSETS` map applies pixel offsets to center labels in readable positions.
- **Label legibility** — CSS `paint-order: stroke fill` with a dark stroke halo ensures labels remain readable against any fill color (dark base, bright blue visited, orange current, green path).
- **Immediate reset abort** — `isRunning` checks after every `await` in `step()` (including inside the neighbor loop) so reset stops animation immediately, not after pending delays.
- **Arrow cleanup on new search** — `clearSearchArrows()` called in `runSearch()` to prevent arrows from leaking between searches.
- **Button disabled during search** — `#runBtn` disabled at search start, re-enabled on completion, reset, or path highlight finish.

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
    If reset: isRunning = false → break immediately

Path backtrack (buildSolution):
  Walk prevMap from end to start → reverse → green path highlight
  Flips exploratory arrows (backwards) → solution arrows (forwards)
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
