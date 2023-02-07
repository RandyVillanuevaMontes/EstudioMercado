package com.example.poligonos

import org.mapsforge.core.graphics.Canvas
import org.mapsforge.map.model.DisplayModel
import org.mapsforge.core.model.BoundingBox
import org.mapsforge.core.model.LatLong
import org.mapsforge.core.model.Point
import org.mapsforge.map.layer.Redrawer

abstract class Layer {
    /**
     * Getter for DisplayModel.
     *
     * @return the display model.
     */
    /**
     * The DisplayModel comes from a MapView, so is generally not known when the layer itself is created. Maybe a better
     * way would be to have a MapView as a parameter when creating a layer.
     *
     * @param displayModel the displayModel to use.
     */
    @get:Synchronized
    @set:Synchronized
    var displayModel: DisplayModel? = null
    private var assignedRedrawer: Redrawer? = null
    private var visible = true

    /**
     * Draws this `Layer` on the given canvas.
     *
     * @param boundingBox  the geographical area which should be drawn.
     * @param zoomLevel    the zoom level at which this `Layer` should draw itself.
     * @param canvas       the canvas on which this `Layer` should draw itself.
     * @param topLeftPoint the top-left pixel position of the canvas relative to the top-left map position.
     */
    abstract fun draw(
        boundingBox: BoundingBox?,
        zoomLevel: Byte,
        canvas: Canvas?,
        topLeftPoint: Point?
    )

    /**
     * Gets the geographic position of this layer element, if it exists.
     *
     *
     * The default implementation of this method returns null.
     *
     * @return the geographic position of this layer element, null otherwise
     */
    val position: LatLong?
        get() = null

    /**
     * @return true if this `Layer` is currently visible, false otherwise. The default value is true.
     */
    fun isVisible(): Boolean {
        return visible
    }

    fun onDestroy() {
        // do nothing
    }

    /**
     * Handles a long press event. A long press event is only triggered if the map was not moved. A return value of true
     * indicates that the long press event has been handled by this overlay and stops its propagation to other overlays.
     *
     *
     * The default implementation of this method does nothing and returns false.
     *
     * @param tapLatLong the geographic position of the long press.
     * @param layerXY    the xy position of the layer element (if available)
     * @param tapXY      the xy position of the tap
     * @return true if the long press event was handled, false otherwise.
     */
    fun onLongPress(tapLatLong: LatLong?, layerXY: Point?, tapXY: Point?): Boolean {
        return false
    }

    /**
     * Handles a scroll event. A return value of true indicates that the scroll event has been handled by this overlay
     * and stops its propagation to other overlays.
     *
     * @param scrollX1 the x position of the first down motion event that started the scrolling.
     * @param scrollY1 the y position of the first down motion event that started the scrolling.
     * @param scrollX2 the x position of the move motion event that triggered the current onScroll.
     * @param scrollY2 the y position of the move motion event that triggered the current onScroll.
     * @return true if the scroll event was handled, false otherwise.
     */
    fun onScroll(scrollX1: Float, scrollY1: Float, scrollX2: Float, scrollY2: Float): Boolean {
        return false
    }

    /**
     * Handles a tap event. A return value of true indicates that the tap event has been handled by this overlay and
     * stops its propagation to other overlays.
     *
     *
     * The default implementation of this method does nothing and returns false.
     *
     * @param tapLatLong the the geographic position of the tap.
     * @param layerXY    the xy position of the layer element (if available)
     * @param tapXY      the xy position of the tap
     * @return true if the tap event was handled, false otherwise.
     */
    fun onTap(tapLatLong: LatLong?, layerXY: Point?, tapXY: Point?): Boolean {
        return false
    }

    /**
     * Requests an asynchronous redrawing of all layers.
     */
    @Synchronized
    fun requestRedraw() {
        if (assignedRedrawer != null) {
            assignedRedrawer!!.redrawLayers()
        }
    }

    /**
     * Sets the visibility flag of this `Layer` to the given value.
     *
     *
     * Note: By default a redraw will take place afterwards.
     */
    fun setVisible(visible: Boolean) {
        setVisible(visible, true)
    }

    /**
     * Sets the visibility flag of this `Layer` to the given value.
     */
    fun setVisible(visible: Boolean, redraw: Boolean) {
        this.visible = visible
        if (redraw) {
            requestRedraw()
        }
    }

    /**
     * Called each time this `Layer` is added to a [Layers] list.
     */
    protected fun onAdd() {
        // do nothing
    }

    /**
     * Called each time this `Layer` is removed from a [Layers] list.
     */
    protected fun onRemove() {
        // do nothing
    }

    @Synchronized
    fun assign(redrawer: Redrawer?) {
        check(assignedRedrawer == null) { "layer already assigned" }
        assignedRedrawer = redrawer
        onAdd()
    }

    @Synchronized
    fun unassign() {
        checkNotNull(assignedRedrawer) { "layer is not assigned" }
        assignedRedrawer = null
        onRemove()
    }
}