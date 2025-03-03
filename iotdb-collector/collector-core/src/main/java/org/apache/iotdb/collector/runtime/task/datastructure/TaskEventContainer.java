package org.apache.iotdb.collector.runtime.task.datastructure;

import org.apache.iotdb.pipe.api.event.Event;

public class TaskEventContainer implements Event {
  private Event event;

  public TaskEventContainer() {}

  public Event getEvent() {
    return event;
  }

  public void setEvent(final Event event) {
    this.event = event;
  }
}
