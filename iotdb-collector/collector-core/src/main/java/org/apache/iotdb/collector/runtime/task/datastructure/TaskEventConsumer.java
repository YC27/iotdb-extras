package org.apache.iotdb.collector.runtime.task.datastructure;

import org.apache.iotdb.pipe.api.PipePlugin;
import org.apache.iotdb.pipe.api.PipeProcessor;
import org.apache.iotdb.pipe.api.PipeSink;

import com.lmax.disruptor.WorkHandler;

public class TaskEventConsumer implements WorkHandler<TaskEventContainer> {

  private final PipePlugin plugin;
  private final TaskEventCollector collector;
  private final TaskEventConsumerController consumerController;

  public TaskEventConsumer(
      final PipePlugin plugin,
      final TaskEventCollector collector,
      final TaskEventConsumerController consumerController) {
    this.plugin = plugin;
    this.collector = collector;
    this.consumerController = consumerController;
  }

  public TaskEventConsumer(
      final PipePlugin plugin, final TaskEventConsumerController consumerController) {
    this(plugin, null, consumerController);
  }

  @Override
  public void onEvent(final TaskEventContainer taskEventContainer) throws Exception {
    if (!consumerController.shouldRun()) {
      return;
    }
    if (plugin instanceof PipeProcessor) {
      ((PipeProcessor) plugin).process(taskEventContainer.getEvent(), this.collector);
    } else if (plugin instanceof PipeSink) {
      ((PipeSink) plugin).transfer(taskEventContainer.getEvent());
    }
  }

  public TaskEventConsumerController getConsumerController() {
    return consumerController;
  }
}
